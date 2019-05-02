package com.jkramr.demowalletserver.service.balance;

import com.jkramr.demowalletapi.grpc.Withdraw;
import com.jkramr.demowalletapi.grpc.WithdrawServiceGrpc;
import com.jkramr.demowalletapi.model.Common;
import com.jkramr.demowalletserver.model.Currency;
import com.jkramr.demowalletserver.model.Wallet;
import com.jkramr.demowalletserver.repository.UserRepository;
import com.jkramr.demowalletserver.repository.WalletRepository;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Supplier;

import static com.jkramr.demowalletapi.grpc.Withdraw.WithdrawResponse.newBuilder;

@GrpcService
@Transactional
public class WithdrawService extends WithdrawServiceGrpc.WithdrawServiceImplBase {

    private WalletRepository walletRepository;
    private UserRepository userRepository;

    @Autowired
    public WithdrawService(WalletRepository walletRepository, UserRepository userRepository) {
        this.walletRepository = walletRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void withdrawFunds(Withdraw.WithdrawRequest request, StreamObserver<Withdraw.WithdrawResponse> responseObserver) {
        Withdraw.WithdrawResponse.Builder response = onInvalidRequest(request)
                .or(userDoesNotExist(request))
                .orElseGet(() -> doWithdraw(request));

        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
    }

    private Optional<Withdraw.WithdrawResponse.Builder> onInvalidRequest(Withdraw.WithdrawRequest request) {
        Withdraw.WithdrawResponse.Builder response = newBuilder();
        if (request.getUserId() <= 0) {
            return Optional.of(response.setDebugMessage(request.getUserId() == 0 ? "user id empty" : "user id invalid"));
        }

        if (!isValidWithdrawAmount(request)) {
            //according to the Task, we don't throw any invalid amount related errors, so just returning empty response in this case
            return Optional.of(response.setDebugMessage("invalid deposit amount"));
        }

        if (!isValidCurrency(request.getCurrency())) {
            //the only possible scenario when this error will be thrown is incompatible versions of api for client and server (demowallet-api-java)
            //and client api contains a currency, which has been removed in server API
            return Optional.of(response.setError(Withdraw.WithdrawResponse.Error.UNKNOWN_CURRENCY));
        }

        return Optional.empty();
    }

    private Supplier<Optional<? extends Withdraw.WithdrawResponse.Builder>> userDoesNotExist(Withdraw.WithdrawRequest request) {
        return () -> {
            Withdraw.WithdrawResponse.Builder response = newBuilder();

            if (userRepository.findById(request.getUserId()).isEmpty()) {
                return Optional.of(response.setDebugMessage("user id does not exist"));
            }

            return Optional.empty();
        };
    }

    @Transactional(rollbackFor = Exception.class)
    Withdraw.WithdrawResponse.Builder doWithdraw(Withdraw.WithdrawRequest request) {
        Withdraw.WithdrawResponse.Builder response = newBuilder();
        int userId = request.getUserId();
        Double depositAmount = request.getAmount();
        Common.Currency currency = request.getCurrency();

        //since no wallet creation specified by the Task, creating one silently on deposit action
        Wallet wallet = walletRepository.findByUserIdAndCurrency(userId, Currency.of(currency))
                .orElseGet(() -> createNewWallet(userId, currency));

        double finalAmount = wallet.getAmount() - depositAmount;
        if (finalAmount < 0) {
            return response.setError(Withdraw.WithdrawResponse.Error.INSUFFICIENT_FUNDS);
        }
        wallet.setAmount(finalAmount);

        Wallet save = walletRepository.save(wallet);
        if (save.getAmount() != finalAmount) {
            response.setDebugMessage(String.format("expected resulting amount %s, was %s", finalAmount, save.getAmount()));
        }

        return response.setDebugMessage("ok");
    }

    private boolean isValidWithdrawAmount(Withdraw.WithdrawRequest request) {
        return request.getAmount() >= 0;
    }

    private boolean isValidCurrency(Common.Currency currency) {
        return Arrays.stream(Currency.values()).map(Currency::name).anyMatch(c -> c.equals(currency.name()));
    }

    private Wallet createNewWallet(Integer userId, Common.Currency currency) {
        Wallet wallet = new Wallet();
        wallet.setUserId(userId);
        wallet.setAmount(0.0);
        wallet.setCurrency(Currency.of(currency));
        return walletRepository.save(wallet);
    }

}


package com.jkramr.demowalletserver.service.balance;

import com.jkramr.demowalletapi.grpc.Deposit;
import com.jkramr.demowalletapi.grpc.DepositServiceGrpc;
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

import static com.jkramr.demowalletapi.grpc.Deposit.DepositResponse.newBuilder;

@GrpcService
@Transactional
public class DepositService extends DepositServiceGrpc.DepositServiceImplBase {

    private WalletRepository walletRepository;
    private UserRepository userRepository;

    @Autowired
    public DepositService(WalletRepository walletRepository, UserRepository userRepository) {
        this.walletRepository = walletRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void depositFunds(Deposit.DepositRequest request, StreamObserver<Deposit.DepositResponse> responseObserver) {
        Deposit.DepositResponse.Builder response = onInvalidRequest(request)
                .or(userDoesNotExist(request))
                .orElseGet(() -> doDeposit(request));

        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
    }

    private Supplier<Optional<? extends Deposit.DepositResponse.Builder>> userDoesNotExist(Deposit.DepositRequest request) {
        return () -> {
            Deposit.DepositResponse.Builder response = newBuilder();

            if (userRepository.findById(request.getUserId()).isEmpty()) {
                return Optional.of(response.setDebugMessage("user id does not exist"));
            }

            return Optional.empty();
        };
    }

    private Optional<Deposit.DepositResponse.Builder> onInvalidRequest(Deposit.DepositRequest request) {
        Deposit.DepositResponse.Builder response = newBuilder();
        if (request.getUserId() <= 0) {
            return Optional.of(response.setDebugMessage(request.getUserId() == 0 ? "user id empty" : "user id invalid"));
        }

        if (!isValidDepositAmount(request)) {
            //according to the Task, we don't throw any invalid amount related errors, so just returning empty response in this case
            return Optional.of(response.setDebugMessage("invalid deposit amount"));
        }

        if (!isValidCurrency(request.getCurrency())) {
            return Optional.of(response.setError(Deposit.DepositResponse.Error.UNKNOWN_CURRENCY));
        }

        return Optional.empty();
    }

    private Deposit.DepositResponse.Builder doDeposit(Deposit.DepositRequest request) {
        Deposit.DepositResponse.Builder response = newBuilder();
        int userId = request.getUserId();
        Common.Currency currency = request.getCurrency();

        boolean isSuccessful = depositToWalletInTransaction(userId, currency, request.getAmount());

        return response.setDebugMessage(isSuccessful ? "ok" : "failed");
    }

    private boolean isValidDepositAmount(Deposit.DepositRequest request) {
        return request.getAmount() >= 0;
    }

    private boolean isValidCurrency(Common.Currency currency) {
        return Arrays.stream(Currency.values()).map(Currency::name).anyMatch(c -> c.equals(currency.name()));
    }

    @Transactional(rollbackFor = Exception.class)
    boolean depositToWalletInTransaction(Integer userId, Common.Currency currency, Double depositAmount) {
        //since no wallet creation specified by the Task, creating one silently on deposit action
        Wallet wallet = walletRepository.findByUserIdAndCurrency(userId, Currency.of(currency))
                .orElseGet(() -> createNewWallet(userId, currency));

        double finalAmount = wallet.getAmount() + depositAmount;
        wallet.setAmount(finalAmount);

        Wallet save = walletRepository.save(wallet);
        if (save.getAmount() != finalAmount) {
            throw new DepositException();
        }

        return true;
    }

    private Wallet createNewWallet(Integer userId, Common.Currency currency) {
        Wallet wallet = new Wallet();
        wallet.setUserId(userId);
        wallet.setAmount(0.0);
        wallet.setCurrency(Currency.of(currency));
        return walletRepository.save(wallet);
    }

    private class DepositException extends RuntimeException {
    }
}


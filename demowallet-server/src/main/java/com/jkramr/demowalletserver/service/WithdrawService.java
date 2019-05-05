package com.jkramr.demowalletserver.service;

import com.jkramr.demowalletapi.grpc.Withdraw;
import com.jkramr.demowalletapi.grpc.WithdrawServiceGrpc;
import com.jkramr.demowalletapi.model.Common;
import com.jkramr.demowalletserver.model.Currency;
import com.jkramr.demowalletserver.model.Wallet;
import com.jkramr.demowalletserver.repository.WalletRepository;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Optional;

import static com.jkramr.demowalletapi.grpc.Withdraw.WithdrawResponse.newBuilder;

@GrpcService
@Transactional(isolation = Isolation.SERIALIZABLE)
public class WithdrawService extends WithdrawServiceGrpc.WithdrawServiceImplBase {

    private WalletRepository walletRepository;
    private Logger logger = LoggerFactory.getLogger(WithdrawService.class);

    @Autowired
    public WithdrawService(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    @Override
    public void withdrawFunds(Withdraw.WithdrawRequest request, StreamObserver<Withdraw.WithdrawResponse> responseObserver) {
        Withdraw.WithdrawResponse response = onInvalidRequest(request)
                .orElseGet(() -> doWithdraw(request));
        logger.info("WITHDRAW user_id={}, {} {} => {}", request.getUserId(), request.getAmount(), request.getCurrency(), response.getDebugMessage());
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    private Optional<Withdraw.WithdrawResponse> onInvalidRequest(Withdraw.WithdrawRequest request) {
        Withdraw.WithdrawResponse.Builder response = newBuilder();
        if (request.getUserId() <= 0) {
            return Optional.of(response.setDebugMessage(request.getUserId() == 0 ? "user id empty" : "user id invalid").build());
        }

        if (!isValidWithdrawAmount(request)) {
            //according to the Task, we don't throw any invalid amount related errors, so just returning empty response in this case
            return Optional.of(response.setDebugMessage("invalid deposit amount").build());
        }

        if (!isValidCurrency(request.getCurrency())) {
            //the only possible scenario when this error will be thrown is incompatible versions of api for client and server (demowallet-api-java)
            //and client api contains a currency, which has been removed in server API
            return Optional.of(response.setError(Withdraw.WithdrawResponse.Error.UNKNOWN_CURRENCY).build());
        }

        return Optional.empty();
    }

    private Withdraw.WithdrawResponse doWithdraw(Withdraw.WithdrawRequest request) {
        Withdraw.WithdrawResponse.Builder response = newBuilder();
        int userId = request.getUserId();
        Double withdrawAmount = request.getAmount();
        Common.Currency currency = request.getCurrency();

        Optional<Wallet> walletWithSufficientFunds = walletRepository.findByUserIdAndCurrency(userId, Currency.of(currency))
                .filter(wallet -> wallet.getBalance() >= withdrawAmount);

        if (walletWithSufficientFunds.isEmpty()) {
            response.setDebugMessage("Insufficient funds");
            return response.setError(Withdraw.WithdrawResponse.Error.INSUFFICIENT_FUNDS).build();
        }

        Wallet wallet = walletWithSufficientFunds.get();
        wallet.debit(withdrawAmount);

        return response.setDebugMessage("Ok").build();
    }

    private boolean isValidWithdrawAmount(Withdraw.WithdrawRequest request) {
        return request.getAmount() >= 0;
    }

    private boolean isValidCurrency(Common.Currency currency) {
        return Arrays.stream(Currency.values()).map(Currency::name).anyMatch(c -> c.equals(currency.name()));
    }

}


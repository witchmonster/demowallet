package com.jkramr.demowalletserver.service;

import com.jkramr.demowalletapi.grpc.Deposit;
import com.jkramr.demowalletapi.grpc.DepositServiceGrpc;
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

import static com.jkramr.demowalletapi.grpc.Deposit.DepositResponse.newBuilder;

@GrpcService
@Transactional(isolation = Isolation.SERIALIZABLE)
public class DepositService extends DepositServiceGrpc.DepositServiceImplBase {

    private WalletRepository walletRepository;
    private Logger logger = LoggerFactory.getLogger(DepositService.class);

    @Autowired
    public DepositService(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    @Override
    public void depositFunds(Deposit.DepositRequest request, StreamObserver<Deposit.DepositResponse> responseObserver) {
        Deposit.DepositResponse.Builder response = onInvalidRequest(request)
                .orElseGet(() -> doDeposit(request));
        logger.info("DEPOSIT user_id={}, {} {} => {}", request.getUserId(), request.getAmount(), request.getCurrency(), response.getDebugMessage());
        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
    }

    private Optional<Deposit.DepositResponse.Builder> onInvalidRequest(Deposit.DepositRequest request) {
        Deposit.DepositResponse.Builder response = newBuilder();
        if (request.getUserId() <= 0) {
            return Optional.of(response.setDebugMessage(request.getUserId() == 0 ? "Empty user_id empty" : "Invalid user_id"));
        }

        if (!isValidDepositAmount(request)) {
            //according to the Task, we don't throw any invalid amount related errors, so just returning empty response in this case
            return Optional.of(response.setDebugMessage("Invalid deposit amount"));
        }

        if (!isValidCurrency(request.getCurrency())) {
            response.setDebugMessage("Unknown currency");
            return Optional.of(response.setError(Deposit.DepositResponse.Error.UNKNOWN_CURRENCY));
        }

        return Optional.empty();
    }

    private Deposit.DepositResponse.Builder doDeposit(Deposit.DepositRequest request) {
        Deposit.DepositResponse.Builder response = newBuilder();
        int userId = request.getUserId();
        Common.Currency currency = request.getCurrency();

        boolean isSuccessful = deposit(userId, currency, request.getAmount());

        return response.setDebugMessage(isSuccessful ? "Ok" : "Failed");
    }

    private boolean isValidDepositAmount(Deposit.DepositRequest request) {
        return request.getAmount() >= 0;
    }

    private boolean isValidCurrency(Common.Currency currency) {
        return Arrays.stream(Currency.values()).map(Currency::name).anyMatch(c -> c.equals(currency.name()));
    }

    boolean deposit(Integer userId, Common.Currency currency, Double depositAmount) {
        //since no wallet creation specified by the Task, creating one silently on deposit action
        Wallet wallet = walletRepository.findByUserIdAndCurrency(userId, Currency.of(currency))
                .orElseGet(() -> createNewWallet(userId, currency));

        double finalAmount = wallet.getBalance() + depositAmount;
        wallet.setBalance(finalAmount);

        return true;
    }

    private Wallet createNewWallet(Integer userId, Common.Currency currency) {
        Wallet wallet = new Wallet();
        wallet.setUserId(userId);
        wallet.setBalance(0.0);
        wallet.setCurrency(Currency.of(currency));
        return walletRepository.save(wallet);
    }

}


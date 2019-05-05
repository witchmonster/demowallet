package com.jkramr.demowalletclient.service;

import com.jkramr.demowalletapi.grpc.*;
import com.jkramr.demowalletapi.model.Common;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

@Component
public class GrpcChannel {

    @GrpcClient("local")
    private RegisterServiceGrpc.RegisterServiceBlockingStub registerService;
    @GrpcClient("local")
    private WithdrawServiceGrpc.WithdrawServiceBlockingStub withdrawService;
    @GrpcClient("local")
    private BalanceServiceGrpc.BalanceServiceBlockingStub balanceService;
    @GrpcClient("local")
    private DepositServiceGrpc.DepositServiceBlockingStub depositService;


    void deposit(int userId, Common.Currency currency, double amount) {
        Deposit.DepositRequest request = Deposit.DepositRequest.newBuilder()
                .setUserId(userId)
                .setCurrency(currency)
                .setAmount(amount)
                .build();
        depositService.depositFunds(request);
    }

    void withdraw(int userId, Common.Currency currency, double amount) {
        Withdraw.WithdrawRequest request = Withdraw.WithdrawRequest.newBuilder()
                .setUserId(userId)
                .setCurrency(currency)
                .setAmount(amount)
                .build();
        withdrawService.withdrawFunds(request);
    }

    void getBalance(int userId) {
        Balance.BalanceRequest request = Balance.BalanceRequest.newBuilder()
                .setUserId(userId)
                .build();
        balanceService.getBalance(request);
    }

    void register(int userId) {
        Register.RegisterRequest request = Register.RegisterRequest.newBuilder()
                .setUserId(userId)
                .build();
        registerService.register(request);
    }
}

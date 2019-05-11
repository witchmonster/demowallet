package com.jkramr.demowalletclient.service.grpc;

import com.jkramr.demowalletapi.grpc.*;
import com.jkramr.demowalletapi.model.Common;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

@Component
public class LocalGrpcChannel implements GrpcChannel {

    @GrpcClient("local")
    private RegisterServiceGrpc.RegisterServiceBlockingStub registerService;
    @GrpcClient("local")
    private WithdrawServiceGrpc.WithdrawServiceBlockingStub withdrawService;
    @GrpcClient("local")
    private BalanceServiceGrpc.BalanceServiceBlockingStub balanceService;
    @GrpcClient("local")
    private DepositServiceGrpc.DepositServiceBlockingStub depositService;

    @Override
    public Deposit.DepositResponse deposit(int userId, Common.Currency currency, double amount) {
        Deposit.DepositRequest request = Deposit.DepositRequest.newBuilder()
                .setUserId(userId)
                .setCurrency(currency)
                .setAmount(amount)
                .build();
        return depositService.depositFunds(request);
    }

    @Override
    public Withdraw.WithdrawResponse withdraw(int userId, Common.Currency currency, double amount) {
        Withdraw.WithdrawRequest request = Withdraw.WithdrawRequest.newBuilder()
                .setUserId(userId)
                .setCurrency(currency)
                .setAmount(amount)
                .build();
        return withdrawService.withdrawFunds(request);
    }

    @Override
    public Balance.BalanceResponse getBalance(int userId) {
        Balance.BalanceRequest request = Balance.BalanceRequest.newBuilder()
                .setUserId(userId)
                .build();
        return balanceService.getBalance(request);
    }

    @Override
    public Register.RegisterResponse register(int userId) {
        Register.RegisterRequest request = Register.RegisterRequest.newBuilder()
                .setUserId(userId)
                .build();
        return registerService.register(request);
    }
}

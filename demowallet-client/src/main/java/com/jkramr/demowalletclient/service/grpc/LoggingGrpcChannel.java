package com.jkramr.demowalletclient.service.grpc;

import com.jkramr.demowalletapi.grpc.Balance;
import com.jkramr.demowalletapi.grpc.Deposit;
import com.jkramr.demowalletapi.grpc.Register;
import com.jkramr.demowalletapi.grpc.Withdraw;
import com.jkramr.demowalletapi.model.Common;

import java.util.concurrent.atomic.AtomicLong;

public class LoggingGrpcChannel implements GrpcChannel {

    private final GrpcChannel channel;

    private AtomicLong callCount = new AtomicLong();

    public LoggingGrpcChannel(GrpcChannel channel) {
        this.channel = channel;
    }

    @Override
    public Deposit.DepositResponse deposit(int userId, Common.Currency currency, double amount) {
        callCount.incrementAndGet();
        return channel.deposit(userId, currency, amount);
    }

    @Override
    public Withdraw.WithdrawResponse withdraw(int userId, Common.Currency currency, double amount) {
        callCount.incrementAndGet();
        return channel.withdraw(userId, currency, amount);
    }

    @Override
    public Balance.BalanceResponse getBalance(int userId) {
        callCount.incrementAndGet();
        return channel.getBalance(userId);
    }

    @Override
    public Register.RegisterResponse register(int userId) {
        callCount.incrementAndGet();
        return channel.register(userId);
    }

    public AtomicLong getCallCount() {
        return callCount;
    }
}

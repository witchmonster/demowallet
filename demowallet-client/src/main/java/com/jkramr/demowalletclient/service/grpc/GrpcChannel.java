package com.jkramr.demowalletclient.service.grpc;

import com.jkramr.demowalletapi.grpc.Balance;
import com.jkramr.demowalletapi.grpc.Deposit;
import com.jkramr.demowalletapi.grpc.Register;
import com.jkramr.demowalletapi.grpc.Withdraw;
import com.jkramr.demowalletapi.model.Common;

public interface GrpcChannel {
    Deposit.DepositResponse deposit(int userId, Common.Currency currency, double amount);

    Withdraw.WithdrawResponse withdraw(int userId, Common.Currency currency, double amount);

    Balance.BalanceResponse getBalance(int userId);

    Register.RegisterResponse register(int userId);
}

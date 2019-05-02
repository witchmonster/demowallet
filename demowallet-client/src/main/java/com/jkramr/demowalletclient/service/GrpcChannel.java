package com.jkramr.demowalletclient.service;

import com.jkramr.demowalletapi.grpc.*;
import com.jkramr.demowalletapi.model.Common;
import lombok.Data;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
public class GrpcChannel {

    @GrpcClient("wallet")
    private WithdrawServiceGrpc.WithdrawServiceBlockingStub withdrawService;
    @GrpcClient("wallet")
    private BalanceServiceGrpc.BalanceServiceBlockingStub balanceService;
    @GrpcClient("wallet")
    private DepositServiceGrpc.DepositServiceBlockingStub depositService;


    public boolean deposit(int userId, Common.Currency currency, double amount) {
        Deposit.DepositRequest request = Deposit.DepositRequest.newBuilder()
                .setUserId(userId)
                .setCurrency(currency)
                .setAmount(amount)
                .build();
        Deposit.DepositResponse response = depositService.depositFunds(request);

        return response.getError() == null;
    }

    public boolean withdraw(int userId, Common.Currency currency, double amount) {
        Withdraw.WithdrawRequest request = Withdraw.WithdrawRequest.newBuilder()
                .setUserId(userId)
                .setCurrency(currency)
                .setAmount(amount)
                .build();
        Withdraw.WithdrawResponse response = withdrawService.withdrawFunds(request);

        return response.getError() == null;
    }

    public List<Balance.WalletBalance> getBalance(int userId) {
        Balance.BalanceRequest request = Balance.BalanceRequest.newBuilder()
                .setUserId(userId)
                .build();
        Balance.BalanceResponse response = balanceService.getBalance(request);

        return response.getBalanceInfoList();
    }
}

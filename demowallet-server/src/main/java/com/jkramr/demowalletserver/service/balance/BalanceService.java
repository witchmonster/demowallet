package com.jkramr.demowalletserver.service.balance;

import com.jkramr.demowalletapi.grpc.Balance;
import com.jkramr.demowalletapi.grpc.BalanceServiceGrpc;
import com.jkramr.demowalletapi.model.Common;
import com.jkramr.demowalletserver.model.Currency;
import com.jkramr.demowalletserver.model.Wallet;
import com.jkramr.demowalletserver.repository.WalletRepository;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

@GrpcService
public class BalanceService extends BalanceServiceGrpc.BalanceServiceImplBase {

    private WalletRepository walletRepository;

    @Autowired
    public BalanceService(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    @Override
    public void getBalance(Balance.BalanceRequest request, StreamObserver<Balance.BalanceResponse> responseObserver) {
        List<Wallet> wallet = walletRepository.findByUserId(request.getUserId());
        List<Balance.WalletBalance> balanceInfo = wallet.stream().map(this::getWalletBalance).collect(Collectors.toList());
        Balance.BalanceResponse response = Balance.BalanceResponse.newBuilder().addAllBalanceInfo(balanceInfo).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    private Balance.WalletBalance getWalletBalance(Wallet wallet) {
        return Balance.WalletBalance.newBuilder().setAmount(wallet.getAmount()).setCurrency(Currency.to(wallet.getCurrency())).build();
    }
}

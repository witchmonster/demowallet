package com.jkramr.demowalletserver.service;

import com.jkramr.demowalletapi.grpc.Balance;
import com.jkramr.demowalletapi.grpc.BalanceServiceGrpc;
import com.jkramr.demowalletserver.model.Currency;
import com.jkramr.demowalletserver.model.Wallet;
import com.jkramr.demowalletserver.repository.WalletRepository;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@GrpcService
@Transactional
public class BalanceService extends BalanceServiceGrpc.BalanceServiceImplBase {

    private WalletRepository walletRepository;
    private Logger logger = LoggerFactory.getLogger(BalanceService.class);

    @Autowired
    public BalanceService(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    @Override
    public void getBalance(Balance.BalanceRequest request, StreamObserver<Balance.BalanceResponse> responseObserver) {
        List<Wallet> wallets = walletRepository.findByUserId(request.getUserId());
        List<Balance.WalletBalance> balanceInfo = wallets.stream().map(this::getWalletBalance).collect(Collectors.toList());
        Balance.BalanceResponse response = Balance.BalanceResponse.newBuilder().addAllBalanceInfo(balanceInfo).build();
        logger.info("GET BALANCE user_id={} => {}", request.getUserId(), response.getBalanceInfoList().stream().map(info -> "{" + info.getAmount() + " " + info.getCurrency() + "}").toArray());
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    private Balance.WalletBalance getWalletBalance(Wallet wallet) {
        return Balance.WalletBalance.newBuilder().setAmount(wallet.getBalance()).setCurrency(Currency.to(wallet.getCurrency())).build();
    }
}

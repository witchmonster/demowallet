package com.jkramr.demowalletserver.controller;

import com.jkramr.demowalletapi.grpc.Balance;
import com.jkramr.demowalletapi.grpc.BalanceServiceGrpc;
import com.jkramr.demowalletserver.service.BalanceService;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@GrpcService
public class BalanceController extends BalanceServiceGrpc.BalanceServiceImplBase {

    private Logger logger = LoggerFactory.getLogger(BalanceService.class);
    private final BalanceService balanceService;

    public BalanceController(BalanceService balanceService) {
        this.balanceService = balanceService;
    }

    @Override
    public void getBalance(Balance.BalanceRequest request, StreamObserver<Balance.BalanceResponse> responseObserver) {
        responseObserver.onNext(processResponse(request));
        responseObserver.onCompleted();
    }

    private Balance.BalanceResponse processResponse(Balance.BalanceRequest request) {
        Balance.BalanceResponse response = balanceService.processRequest(request);
        logger.info("GET BALANCE user_id={} => {}",
                request.getUserId(),
                response.getBalanceInfoList().stream()
                        .map(info -> "{" + info.getAmount() + " " + info.getCurrency() + "}")
                        .toArray());
        return response;
    }
}

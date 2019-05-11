package com.jkramr.demowalletserver.controller;

import com.jkramr.demowalletapi.grpc.Withdraw;
import com.jkramr.demowalletapi.grpc.WithdrawServiceGrpc;
import com.jkramr.demowalletserver.service.Service;
import com.jkramr.demowalletserver.service.WithdrawService;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

@GrpcService
@Transactional
public class WithdrawController extends WithdrawServiceGrpc.WithdrawServiceImplBase {

    private Logger logger = LoggerFactory.getLogger(WithdrawService.class);

    private final Service<Withdraw.WithdrawRequest, Withdraw.WithdrawResponse> withdrawService;

    public WithdrawController(Service<Withdraw.WithdrawRequest, Withdraw.WithdrawResponse> withdrawService) {
        this.withdrawService = withdrawService;
    }

    @Override
    public void withdrawFunds(Withdraw.WithdrawRequest request, StreamObserver<Withdraw.WithdrawResponse> responseObserver) {
        Withdraw.WithdrawResponse response = withdrawService.processRequest(request);
        logger.info("WITHDRAW user_id={}, {} {} => {}", request.getUserId(), request.getAmount(), request.getCurrency(), response.getCode());
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}

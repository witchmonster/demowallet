package com.jkramr.demowalletserver.controller;

import com.jkramr.demowalletapi.grpc.Deposit;
import com.jkramr.demowalletapi.grpc.DepositServiceGrpc;
import com.jkramr.demowalletapi.grpc.Register;
import com.jkramr.demowalletserver.service.Service;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.transaction.annotation.Transactional;

@GrpcService
@Transactional
public class DepositController extends DepositServiceGrpc.DepositServiceImplBase {

    private final Service<Deposit.DepositRequest, Deposit.DepositResponse> depositService;

    public DepositController(Service<Deposit.DepositRequest, Deposit.DepositResponse> depositService) {
        this.depositService = depositService;
    }

    @Override
    public void depositFunds(Deposit.DepositRequest request, StreamObserver<Deposit.DepositResponse> responseObserver) {
        Deposit.DepositResponse response = depositService.processRequest(request);
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}

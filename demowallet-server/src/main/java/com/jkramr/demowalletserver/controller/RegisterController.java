package com.jkramr.demowalletserver.controller;

import com.jkramr.demowalletapi.grpc.Register;
import com.jkramr.demowalletapi.grpc.RegisterServiceGrpc;
import com.jkramr.demowalletserver.service.RegisterService;
import com.jkramr.demowalletserver.service.Service;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@GrpcService
public class RegisterController extends RegisterServiceGrpc.RegisterServiceImplBase {

    private Logger logger = LoggerFactory.getLogger(RegisterService.class);

    private final Service<Register.RegisterRequest, Register.RegisterResponse> registerService;

    public RegisterController(Service<Register.RegisterRequest, Register.RegisterResponse> registerService) {
        this.registerService = registerService;
    }

    @Override
    public void register(Register.RegisterRequest request, StreamObserver<Register.RegisterResponse> responseObserver) {
        Register.RegisterResponse response = registerService.processRequest(request);
        logger.info("REGISTERED user_id={}", request.getUserId());
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}

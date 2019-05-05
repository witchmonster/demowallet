package com.jkramr.demowalletserver.service;

import com.jkramr.demowalletapi.grpc.Register;
import com.jkramr.demowalletapi.grpc.RegisterServiceGrpc;
import com.jkramr.demowalletserver.model.Currency;
import com.jkramr.demowalletserver.model.Wallet;
import com.jkramr.demowalletserver.repository.WalletRepository;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@GrpcService
@Transactional
public class RegisterService extends RegisterServiceGrpc.RegisterServiceImplBase {

    private WalletRepository walletRepository;
    private Logger logger = LoggerFactory.getLogger(RegisterService.class);

    @Autowired
    public RegisterService(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    @Override
    public void register(Register.RegisterRequest request, StreamObserver<Register.RegisterResponse> responseObserver) {
        Register.RegisterResponse response = ifAlreadyExists(request)
                .orElseGet(() -> doRegister(request));
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    private Register.RegisterResponse doRegister(Register.RegisterRequest request) {
        Register.RegisterResponse.Builder response = Register.RegisterResponse.newBuilder();
        List<Wallet> wallets = Arrays.stream(Currency.values()).map(currency -> createNewWallet(request.getUserId(), currency)).collect(Collectors.toList());
        logger.info("REGISTERED user_id={} => {}", request.getUserId(), wallets.stream().map(info -> "{" + info.getBalance() + " " + info.getCurrency() + "}").toArray());
        return response.setDebugMessage("Ok").build();
    }

    private Optional<Register.RegisterResponse> ifAlreadyExists(Register.RegisterRequest request) {
        Register.RegisterResponse.Builder response = Register.RegisterResponse.newBuilder();
        List<Wallet> wallets = walletRepository.findByUserId(request.getUserId());
        if (!wallets.isEmpty()) {
            logger.info("ALREADY_EXISTS user_id={}", request.getUserId());
            return Optional.of(response.setDebugMessage("Already exists").build());
        }
        return Optional.empty();
    }

    private Wallet createNewWallet(Integer userId, Currency currency) {
        Wallet wallet = new Wallet();
        wallet.setUserId(userId);
        wallet.setBalance(0.0);
        wallet.setCurrency(currency);
        return walletRepository.save(wallet);
    }
}

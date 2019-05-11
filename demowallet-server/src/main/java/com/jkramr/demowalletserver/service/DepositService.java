package com.jkramr.demowalletserver.service;

import com.jkramr.demowalletapi.grpc.Deposit;
import com.jkramr.demowalletapi.grpc.Deposit.DepositRequest;
import com.jkramr.demowalletapi.grpc.Deposit.DepositResponse;
import com.jkramr.demowalletserver.repository.WalletManagedRepository;
import com.jkramr.demowalletserver.repository.WalletRepository;
import com.jkramr.demowalletserver.service.validators.DepositRequestValidator;
import com.jkramr.demowalletserver.model.Currency;
import com.jkramr.demowalletserver.model.Wallet;
import com.jkramr.demowalletserver.systemdata.SystemData;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Optional;

import static com.jkramr.demowalletapi.grpc.Deposit.DepositResponse.newBuilder;

@Component
public class DepositService extends AbstractService<DepositRequest, DepositResponse> {

    private WalletRepository walletRepository;
    private final TransactionTemplate transactionTemplate;
    private final SystemData systemData;

    public DepositService(
            WalletManagedRepository walletRepository,
            DepositRequestValidator requestValidator,
            PlatformTransactionManager transactionManager,
            SystemData systemData
    ) {
        super(requestValidator);
        this.walletRepository = walletRepository;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
        this.systemData = systemData;
    }

    @Override
    protected DepositResponse doProcess(DepositRequest request) {
        DepositResponse.Builder response = newBuilder();
        if (systemData.getCurrency(request.getCurrency().name()).isEmpty()) {
            return response.setCode(Deposit.DepositResponse.ResponseCode.UNKNOWN_CURRENCY).build();
        }

        transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_SERIALIZABLE);
        return transactionTemplate.execute(status -> {
            Optional<Wallet> wallet = walletRepository.findByUserIdAndCurrency(request.getUserId(), Currency.of(request.getCurrency()));

            if (wallet.isEmpty()) {
                return response.setCode(DepositResponse.ResponseCode.WALLET_NOT_FOUND).build();
            }

            wallet.ifPresent(w -> walletRepository.credit(w, request.getAmount()));
            return response.setCode(DepositResponse.ResponseCode.OK).build();
        });
    }

}


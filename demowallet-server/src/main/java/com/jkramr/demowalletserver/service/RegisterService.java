package com.jkramr.demowalletserver.service;

import com.jkramr.demowalletapi.grpc.Register;
import com.jkramr.demowalletserver.repository.WalletManagedRepository;
import com.jkramr.demowalletserver.repository.WalletRepository;
import com.jkramr.demowalletserver.service.validators.RegisterRequestValidator;
import com.jkramr.demowalletserver.systemdata.SystemData;
import com.jkramr.demowalletserver.model.Currency;
import com.jkramr.demowalletserver.model.Wallet;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Optional;

@Component
public class RegisterService extends AbstractService<Register.RegisterRequest, Register.RegisterResponse> {

    private final WalletRepository walletRepository;
    private final TransactionTemplate transactionTemplate;
    private final SystemData systemData;

    public RegisterService(
            WalletManagedRepository walletRepository,
            RegisterRequestValidator requestValidator,
            PlatformTransactionManager transactionManager,
            SystemData systemData
    ) {
        super(requestValidator);
        this.walletRepository = walletRepository;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
        this.systemData = systemData;
    }

    @Override
    protected Register.RegisterResponse doProcess(Register.RegisterRequest request) {
        Register.RegisterResponse.Builder response = Register.RegisterResponse.newBuilder();
        transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_SERIALIZABLE);
        return transactionTemplate.execute(status -> {
            systemData.getAllCurrencies().forEach(currency -> {
                Integer userId = request.getUserId();
                Optional<Wallet> byUserIdAndCurrency = walletRepository.findByUserIdAndCurrency(userId, currency);
                byUserIdAndCurrency.orElseGet(() -> createNewWallet(userId, currency));
            });
            return response.setCode(Register.RegisterResponse.ResponseCode.OK).build();
        });
    }

    private Wallet createNewWallet(Integer userId, Currency currency) {
        return walletRepository.insertOne(userId, currency);
    }
}

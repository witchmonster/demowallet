package com.jkramr.demowalletserver.service;

import com.jkramr.demowalletapi.grpc.Withdraw;
import com.jkramr.demowalletserver.repository.WalletManagedRepository;
import com.jkramr.demowalletserver.model.Currency;
import com.jkramr.demowalletserver.model.Wallet;
import com.jkramr.demowalletserver.repository.WalletRepository;
import com.jkramr.demowalletserver.service.validators.WithdrawRequestValidator;
import com.jkramr.demowalletserver.systemdata.SystemData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Optional;

import static com.jkramr.demowalletapi.grpc.Withdraw.WithdrawResponse.newBuilder;

@Component
public class WithdrawService extends AbstractService<Withdraw.WithdrawRequest, Withdraw.WithdrawResponse> {

    private WalletRepository walletRepository;
    private final TransactionTemplate transactionTemplate;
    private final SystemData systemData;

    @Autowired
    public WithdrawService(
            WalletManagedRepository walletRepository,
            WithdrawRequestValidator requestValidator,
            PlatformTransactionManager transactionManager,
            SystemData systemData
    ) {
        super(requestValidator);
        this.walletRepository = walletRepository;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
        this.systemData = systemData;
    }

    @Override
    protected Withdraw.WithdrawResponse doProcess(Withdraw.WithdrawRequest request) {
        Withdraw.WithdrawResponse.Builder response = newBuilder();
        if (systemData.getCurrency(request.getCurrency().name()).isEmpty()) {
            return response.setCode(Withdraw.WithdrawResponse.ResponseCode.UNKNOWN_CURRENCY).build();
        }
        transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_SERIALIZABLE);
        return transactionTemplate.execute(status -> {
            Optional<Wallet> wallet = walletRepository.findByUserIdAndCurrency(request.getUserId(), Currency.of(request.getCurrency()));

            if (wallet.isEmpty()) {
                return response.setCode(Withdraw.WithdrawResponse.ResponseCode.WALLET_NOT_FOUND).build();
            }

            if (wallet.filter(w -> w.getBalance() >= (Double) request.getAmount()).isEmpty()) {
                return response.setCode(Withdraw.WithdrawResponse.ResponseCode.INSUFFICIENT_FUNDS).build();
            }

            wallet.ifPresent(w -> walletRepository.debit(w, request.getAmount()));

            return response.setCode(Withdraw.WithdrawResponse.ResponseCode.OK).build();
        });
    }

}


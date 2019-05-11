package com.jkramr.demowalletserver.service;

import com.jkramr.demowalletapi.grpc.Balance;
import com.jkramr.demowalletserver.repository.WalletManagedRepository;
import com.jkramr.demowalletserver.repository.WalletRepository;
import com.jkramr.demowalletserver.service.validators.BalanceRequestValidator;
import com.jkramr.demowalletserver.model.Currency;
import com.jkramr.demowalletserver.model.Wallet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class BalanceService extends AbstractService<Balance.BalanceRequest, Balance.BalanceResponse> {

    private WalletRepository walletRepository;
    private final TransactionTemplate transactionTemplate;

    @Autowired
    public BalanceService(
            WalletManagedRepository walletRepository,
            BalanceRequestValidator requestValidator,
            PlatformTransactionManager transactionManager
    ) {
        super(requestValidator);
        this.walletRepository = walletRepository;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    @Override
    protected Balance.BalanceResponse doProcess(Balance.BalanceRequest request) {
        transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_SERIALIZABLE);
        return transactionTemplate.execute(status -> {
            List<Wallet> wallets = walletRepository.findByUserId(request.getUserId());
            List<Balance.BalanceInfo> balanceInfo = wallets.stream()
                    .map(this::getWalletBalance)
                    .collect(Collectors.toList());
            return Balance.BalanceResponse.newBuilder().addAllBalanceInfo(balanceInfo).build();
        });
    }

    private Balance.BalanceInfo getWalletBalance(Wallet wallet) {
        return Balance.BalanceInfo.newBuilder().setAmount(wallet.getBalance()).setCurrency(Currency.to(wallet.getCurrency())).build();
    }
}

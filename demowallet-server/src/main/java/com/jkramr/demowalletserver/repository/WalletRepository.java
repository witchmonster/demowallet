package com.jkramr.demowalletserver.repository;

import com.jkramr.demowalletserver.model.Currency;
import com.jkramr.demowalletserver.model.Wallet;
import java.util.List;
import java.util.Optional;

public interface WalletRepository {
    List<Wallet> findByUserId(Integer userId);

    Optional<Wallet> findByUserIdAndCurrency(Integer userId, Currency currency);

    Wallet insertOne(Integer userId, Currency currency);

    Wallet credit(Wallet wallet, double amount);

    Wallet debit(Wallet wallet, double amount);

    void deleteAll();

}

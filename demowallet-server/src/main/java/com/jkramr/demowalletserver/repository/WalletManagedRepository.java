package com.jkramr.demowalletserver.repository;

import com.jkramr.demowalletserver.model.Currency;
import com.jkramr.demowalletserver.model.Wallet;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class WalletManagedRepository implements WalletRepository {

    private final WalletCrudRepository walletCrudRepository;

    public WalletManagedRepository(WalletCrudRepository walletCrudRepository) {
        this.walletCrudRepository = walletCrudRepository;
    }

    @Override
    public List<Wallet> findByUserId(Integer userId) {
        return walletCrudRepository.findByUserId(userId);
    }

    @Override
    public Optional<Wallet> findByUserIdAndCurrency(Integer userId, Currency currency) {
        return walletCrudRepository.findByUserIdAndCurrency(userId, currency);
    }

    @Override
    public Wallet insertOne(Integer userId, Currency currency) {
        Wallet newWallet = new Wallet();
        newWallet.setUserId(userId);
        newWallet.setBalance(0.0);
        newWallet.setCurrency(currency);
        return walletCrudRepository.save(newWallet);
    }

    @Override
    public Wallet credit(Wallet wallet, double amount) {
        wallet.setBalance(wallet.getBalance() + amount);
        return wallet;
    }

    @Override
    public Wallet debit(Wallet wallet, double amount) {
        if (wallet.getBalance() >= amount) {
            wallet.setBalance(wallet.getBalance() - amount);
        }
        return wallet;
    }

    @Override
    public void deleteAll() {
        walletCrudRepository.deleteAll();
    }

}

package com.jkramr.demowalletserver.repository;

import com.jkramr.demowalletserver.model.Currency;
import com.jkramr.demowalletserver.model.Wallet;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.CrudRepository;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

public interface WalletRepository extends CrudRepository<Wallet, Integer> {

    List<Wallet> findByUserId(Integer userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Wallet> findByUserIdAndCurrency(Integer userId, Currency currency);
}

package com.jkramr.demowalletserver.service;


import com.jkramr.demowalletapi.grpc.Balance;
import com.jkramr.demowalletapi.model.Common;
import com.jkramr.demowalletserver.model.Currency;
import com.jkramr.demowalletserver.model.Wallet;
import com.jkramr.demowalletserver.repository.WalletManagedRepository;
import com.jkramr.demowalletserver.service.validators.BalanceRequestValidator;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static com.jkramr.demowalletapi.grpc.Balance.BalanceRequest.newBuilder;
import static com.jkramr.demowalletapi.grpc.Balance.BalanceResponse.ResponseCode.INVALID_REQUEST;

@RunWith(MockitoJUnitRunner.class)
public class BalanceServiceTest {
    @Mock
    BalanceRequestValidator requestValidator;
    @Mock
    WalletManagedRepository repository;
    @Mock
    PlatformTransactionManager transactionManager;
    @InjectMocks
    BalanceService balanceService;

    @Test
    public void givenValidRequestAndNoRecordsInDatabaseShouldReturnEmptyResultList() {
        Mockito.when(repository.findByUserId(1)).thenReturn(Collections.emptyList());
        Balance.BalanceResponse balanceResponse = balanceService.processRequest(Balance.BalanceRequest.newBuilder().setUserId(1).build());
        Assert.assertEquals(Balance.BalanceResponse.ResponseCode.OK, balanceResponse.getCode());
        Assert.assertTrue(balanceResponse.getBalanceInfoList().isEmpty());
    }

    @Test
    public void givenValidRequestShouldReturnBalancesFromDatabase() {
        Mockito.when(repository.findByUserId(1)).thenReturn(Arrays.asList(
                new Wallet(1, Currency.USD, 100),
                new Wallet(1, Currency.EUR, 200),
                new Wallet(1, Currency.GBP, 300)
        ));
        Balance.BalanceResponse balanceResponse = balanceService.processRequest(Balance.BalanceRequest.newBuilder().setUserId(1).build());
        Assert.assertEquals(Balance.BalanceResponse.ResponseCode.OK, balanceResponse.getCode());
        Assert.assertEquals(Arrays.asList(
                Balance.BalanceInfo.newBuilder().setAmount(100).setCurrency(Common.Currency.USD).build(),
                Balance.BalanceInfo.newBuilder().setAmount(200).setCurrency(Common.Currency.EUR).build(),
                Balance.BalanceInfo.newBuilder().setAmount(300).setCurrency(Common.Currency.GBP).build()
        ), balanceResponse.getBalanceInfoList());
    }

    @Test
    public void givenInvalidUserIdShouldReturnInvalidRequestCode() {
        Balance.BalanceRequest request = newBuilder().setUserId(-1).build();
        Mockito.when(requestValidator.onInvalidRequest(request))
                .thenReturn(Optional.of(Balance.BalanceResponse.newBuilder().setCode(INVALID_REQUEST).build()));
        Balance.BalanceResponse balanceResponse = balanceService.processRequest(request);
        Assert.assertEquals(INVALID_REQUEST, balanceResponse.getCode());
    }
}
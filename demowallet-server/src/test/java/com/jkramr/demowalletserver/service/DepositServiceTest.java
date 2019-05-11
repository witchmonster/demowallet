package com.jkramr.demowalletserver.service;

import com.jkramr.demowalletapi.grpc.Deposit;
import com.jkramr.demowalletapi.model.Common;
import com.jkramr.demowalletserver.model.Currency;
import com.jkramr.demowalletserver.model.Wallet;
import com.jkramr.demowalletserver.repository.WalletManagedRepository;
import com.jkramr.demowalletserver.service.validators.DepositRequestValidator;
import com.jkramr.demowalletserver.systemdata.SystemData;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Optional;

import static com.jkramr.demowalletapi.grpc.Deposit.DepositResponse.ResponseCode.INVALID_REQUEST;
import static com.jkramr.demowalletapi.grpc.Deposit.DepositResponse.ResponseCode.UNKNOWN_CURRENCY;

@RunWith(MockitoJUnitRunner.class)
public class DepositServiceTest {

    @Mock
    DepositRequestValidator requestValidator;
    @Mock
    SystemData systemData;
    @Mock
    WalletManagedRepository repository;
    @Mock
    PlatformTransactionManager transactionManager;

    @InjectMocks
    DepositService depositService;

    @Test
    public void givenValidRequestShouldDepositAmount() {
        Wallet wallet = new Wallet(1, Currency.USD, 100);
        Mockito.when(repository.findByUserIdAndCurrency(1, Currency.USD))
                .thenReturn(Optional.of(wallet));
        Mockito.when(systemData.getCurrency("USD")).thenReturn(Optional.of(Currency.USD));
        int depositAmount = 200;
        Deposit.DepositResponse depositResponse = depositService.doProcess(Deposit.DepositRequest.newBuilder()
                .setUserId(1)
                .setCurrency(Common.Currency.USD)
                .setAmount(depositAmount)
                .build()
        );
        Mockito.verify(repository).credit(wallet, depositAmount);
        Assert.assertEquals(Deposit.DepositResponse.ResponseCode.OK, depositResponse.getCode());
    }

    @Test
    public void givenUnknownToServerCurrencyShouldReturnUnknownCurrencyError() {
        Mockito.when(systemData.getCurrency("USD")).thenReturn(Optional.empty());
        Deposit.DepositResponse depositResponse = depositService.doProcess(Deposit.DepositRequest.newBuilder()
                .setUserId(1)
                .setCurrency(Common.Currency.USD)
                .setAmount(200)
                .build()
        );
        Assert.assertEquals(UNKNOWN_CURRENCY, depositResponse.getCode());
    }

    @Test
    public void givenInvalidRequestShouldReturnInvalidRequestCode() {
        Deposit.DepositRequest request = Deposit.DepositRequest.newBuilder().setUserId(-1).setCurrency(Common.Currency.USD).setAmount(200).build();
        Mockito.when(requestValidator.onInvalidRequest(request))
                .thenReturn(Optional.of(Deposit.DepositResponse.newBuilder().setCode(INVALID_REQUEST).build()));
        Deposit.DepositResponse depositResponse = depositService.processRequest(request);
        Assert.assertEquals(INVALID_REQUEST, depositResponse.getCode());
    }
}
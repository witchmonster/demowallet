package com.jkramr.demowalletserver.service;

import com.jkramr.demowalletapi.grpc.Withdraw;
import com.jkramr.demowalletapi.model.Common;
import com.jkramr.demowalletserver.model.Currency;
import com.jkramr.demowalletserver.model.Wallet;
import com.jkramr.demowalletserver.repository.WalletManagedRepository;
import com.jkramr.demowalletserver.service.validators.WithdrawRequestValidator;
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

import static com.jkramr.demowalletapi.grpc.Withdraw.WithdrawResponse.ResponseCode.INVALID_REQUEST;
import static com.jkramr.demowalletapi.grpc.Withdraw.WithdrawResponse.ResponseCode.UNKNOWN_CURRENCY;

@RunWith(MockitoJUnitRunner.class)
public class WithdrawServiceTest {

    @Mock
    WithdrawRequestValidator requestValidator;
    @Mock
    SystemData systemData;
    @Mock
    WalletManagedRepository repository;
    @Mock
    PlatformTransactionManager transactionManager;

    @InjectMocks
    WithdrawService withdrawService;

    @Test
    public void givenValidRequestShouldWithdrawAmount() {
        Wallet wallet = new Wallet(1, Currency.USD, 200);
        Mockito.when(repository.findByUserIdAndCurrency(1, Currency.USD))
                .thenReturn(Optional.of(wallet));
        Mockito.when(systemData.getCurrency("USD")).thenReturn(Optional.of(Currency.USD));
        int withdrawAmount = 100;
        Withdraw.WithdrawResponse withdrawResponse = withdrawService.doProcess(Withdraw.WithdrawRequest.newBuilder()
                .setUserId(1)
                .setCurrency(Common.Currency.USD)
                .setAmount(withdrawAmount)
                .build()
        );
        Mockito.verify(repository).debit(wallet, withdrawAmount);
        Assert.assertEquals(Withdraw.WithdrawResponse.ResponseCode.OK, withdrawResponse.getCode());
    }

    @Test
    public void givenBalanceTooLowShouldReturnInsufficientFunds() {
        Wallet wallet = new Wallet(1, Currency.USD, 100);
        Mockito.when(repository.findByUserIdAndCurrency(1, Currency.USD))
                .thenReturn(Optional.of(wallet));
        Mockito.when(systemData.getCurrency("USD")).thenReturn(Optional.of(Currency.USD));
        int withdrawAmount = 200;
        Withdraw.WithdrawResponse withdrawResponse = withdrawService.doProcess(Withdraw.WithdrawRequest.newBuilder()
                .setUserId(1)
                .setCurrency(Common.Currency.USD)
                .setAmount(withdrawAmount)
                .build()
        );
        Mockito.verify(repository, Mockito.times(0)).debit(wallet, withdrawAmount);
        Assert.assertEquals(Withdraw.WithdrawResponse.ResponseCode.INSUFFICIENT_FUNDS, withdrawResponse.getCode());
    }

    @Test
    public void givenUnknownToServerCurrencyShouldReturnUnknownCurrencyError() {
        Mockito.when(systemData.getCurrency("USD")).thenReturn(Optional.empty());
        Withdraw.WithdrawResponse withdrawResponse = withdrawService.doProcess(Withdraw.WithdrawRequest.newBuilder()
                .setUserId(1)
                .setCurrency(Common.Currency.USD)
                .setAmount(200)
                .build()
        );
        Assert.assertEquals(UNKNOWN_CURRENCY, withdrawResponse.getCode());
    }

    @Test
    public void givenInvalidRequestShouldReturnInvalidRequestCode() {
        Withdraw.WithdrawRequest request = Withdraw.WithdrawRequest.newBuilder().setUserId(-1).setCurrency(Common.Currency.USD).setAmount(200).build();
        Mockito.when(requestValidator.onInvalidRequest(request))
                .thenReturn(Optional.of(Withdraw.WithdrawResponse.newBuilder().setCode(INVALID_REQUEST).build()));
        Withdraw.WithdrawResponse WithdrawResponse = withdrawService.processRequest(request);
        Assert.assertEquals(INVALID_REQUEST, WithdrawResponse.getCode());
    }
}
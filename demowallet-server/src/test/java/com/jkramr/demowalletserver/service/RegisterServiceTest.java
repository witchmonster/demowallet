package com.jkramr.demowalletserver.service;

import com.jkramr.demowalletapi.grpc.Register;
import com.jkramr.demowalletserver.model.Currency;
import com.jkramr.demowalletserver.model.Wallet;
import com.jkramr.demowalletserver.repository.WalletManagedRepository;
import com.jkramr.demowalletserver.service.validators.RegisterRequestValidator;
import com.jkramr.demowalletserver.systemdata.SystemData;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Arrays;
import java.util.Optional;

import static com.jkramr.demowalletapi.grpc.Register.RegisterResponse.ResponseCode.INVALID_REQUEST;

@RunWith(MockitoJUnitRunner.class)
public class RegisterServiceTest {

    @Mock
    RegisterRequestValidator requestValidator;
    @Mock
    WalletManagedRepository repository;
    @Mock
    PlatformTransactionManager transactionManager;
    @Mock
    SystemData systemData;
    @InjectMocks
    RegisterService registerService;

    @Test
    public void givenValidRequestAndNoRowsShouldRegister() {
        int userId = 1;
        Mockito.when(repository.findByUserIdAndCurrency(userId, Currency.USD))
                .thenReturn(Optional.empty());
        Mockito.when(repository.findByUserIdAndCurrency(userId, Currency.EUR))
                .thenReturn(Optional.empty());
        Mockito.when(repository.findByUserIdAndCurrency(userId, Currency.GBP))
                .thenReturn(Optional.empty());
        Mockito.when(systemData.getAllCurrencies()).thenReturn(Arrays.asList(Currency.USD, Currency.EUR, Currency.GBP));
        Register.RegisterResponse depositResponse = registerService.doProcess(Register.RegisterRequest.newBuilder()
                .setUserId(userId)
                .build()
        );
        Mockito.verify(repository).insertOne(userId, Currency.USD);
        Mockito.verify(repository).insertOne(userId, Currency.EUR);
        Mockito.verify(repository).insertOne(userId, Currency.GBP);
        Assert.assertEquals(Register.RegisterResponse.ResponseCode.OK, depositResponse.getCode());
    }

    @Test
    public void givenValidRequestAndAlreadyExistsShouldBeIdempotent() {
        int userId = 1;
        Wallet usdWallet = new Wallet(userId, Currency.USD, 0);
        Wallet eurWallet = new Wallet(userId, Currency.EUR, 0);
        Wallet gpbWallet = new Wallet(userId, Currency.GBP, 0);
        Mockito.when(repository.findByUserIdAndCurrency(userId, Currency.USD))
                .thenReturn(Optional.of(usdWallet));
        Mockito.when(repository.findByUserIdAndCurrency(userId, Currency.EUR))
                .thenReturn(Optional.of(eurWallet));
        Mockito.when(repository.findByUserIdAndCurrency(userId, Currency.GBP))
                .thenReturn(Optional.of(gpbWallet));
        Mockito.when(systemData.getAllCurrencies()).thenReturn(Arrays.asList(Currency.USD, Currency.EUR, Currency.GBP));
        Register.RegisterResponse depositResponse = registerService.doProcess(Register.RegisterRequest.newBuilder()
                .setUserId(userId)
                .build()
        );
        Mockito.verify(repository, Mockito.times(0)).insertOne(userId, Currency.USD);
        Mockito.verify(repository, Mockito.times(0)).insertOne(userId, Currency.EUR);
        Mockito.verify(repository, Mockito.times(0)).insertOne(userId, Currency.GBP);
        Assert.assertEquals(Register.RegisterResponse.ResponseCode.OK, depositResponse.getCode());
    }

    @Test
    public void givenInvalidRequestShouldReturnInvalidRequestCode() {
        int invalidUserId = -1;
        Register.RegisterRequest request = Register.RegisterRequest.newBuilder().setUserId(invalidUserId).build();
        Mockito.when(requestValidator.onInvalidRequest(request))
                .thenReturn(Optional.of(Register.RegisterResponse.newBuilder().setCode(INVALID_REQUEST).build()));
        Register.RegisterResponse depositResponse = registerService.processRequest(request);
        Assert.assertEquals(INVALID_REQUEST, depositResponse.getCode());
    }
}
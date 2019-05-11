package com.jkramr.demowalletserver.integration;

import com.jkramr.demowalletapi.grpc.*;
import com.jkramr.demowalletapi.model.Common;
import com.jkramr.demowalletserver.repository.WalletRepository;
import com.jkramr.demowalletserver.systemdata.*;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest
public class DemowalletIntegrationTests {

    @GrpcClient("wallet")
    private RegisterServiceGrpc.RegisterServiceBlockingStub registerStub;

    @GrpcClient("wallet")
    private BalanceServiceGrpc.BalanceServiceBlockingStub balanceStub;

    @GrpcClient("wallet")
    private DepositServiceGrpc.DepositServiceBlockingStub depositStub;

    @GrpcClient("wallet")
    private WithdrawServiceGrpc.WithdrawServiceBlockingStub withdrawStub;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private SystemData systemData;

    @Before
    public void setUp() {
        //given there's no registration method described in the Task, I assume users are registered prior to any task execution
        walletRepository.deleteAll();
        addWalletsForUser(1);
        addWalletsForUser(2);
    }

    private void addWalletsForUser(Integer id) {
        systemData.getAllCurrencies()
                .forEach(currency -> walletRepository.insertOne(id, currency));
    }

    @Test
    public void contextLoads() {
    }

    @Test
    public void givenNewUserShouldRegister() {
        register(3).assertSuccessful();
        getBalance(3)
                .forCurrency(Common.Currency.EUR).amountEquals(0)
                .forCurrency(Common.Currency.USD).amountEquals(0)
                .forCurrency(Common.Currency.GBP).amountEquals(0);
    }

    @Test
    public void taskSpecifiedIntegrationTest() {
        withdrawFunds(1, Common.Currency.USD, 200).assertInsufficientFunds();
        depositFunds(1, Common.Currency.USD, 100).assertSuccessful();
        getBalance(1)
                .forCurrency(Common.Currency.USD).amountEquals(100);
        withdrawFunds(1, Common.Currency.USD, 200).assertInsufficientFunds();
        depositFunds(1, Common.Currency.EUR, 100).assertSuccessful();
        getBalance(1)
                .forCurrency(Common.Currency.USD).amountEquals(100)
                .forCurrency(Common.Currency.EUR).amountEquals(100);
        withdrawFunds(1, Common.Currency.USD, 200).assertInsufficientFunds();
        depositFunds(1, Common.Currency.USD, 100).assertSuccessful();
        withdrawFunds(1, Common.Currency.USD, 200).assertSuccessful();
        getBalance(1)
                .forCurrency(Common.Currency.USD).amountEquals(0)
                .forCurrency(Common.Currency.EUR).amountEquals(100);
        withdrawFunds(1, Common.Currency.USD, 200).assertInsufficientFunds();
    }

    @Test
    public void givenUnregisteredUserShouldReturnWalletNotFound() {
        getBalance(4).assertResponseIsEmpty();
        depositFunds(4, Common.Currency.USD, 100).assertWalletNotFound();
        withdrawFunds(4, Common.Currency.USD, 100).assertWalletNotFound();
    }

    private DepositTestResult depositFunds(int userId, Common.Currency currency, double amount) {
        Deposit.DepositRequest depositRequest = Deposit.DepositRequest.newBuilder()
                .setUserId(userId)
                .setAmount(amount)
                .setCurrency(currency)
                .build();
        return new DepositTestResult(depositStub.depositFunds(depositRequest));
    }

    private WithdrawTestResult withdrawFunds(int userId, Common.Currency currency, double amount) {
        Withdraw.WithdrawRequest withdrawRequest = Withdraw.WithdrawRequest.newBuilder()
                .setUserId(userId)
                .setCurrency(currency)
                .setAmount(amount)
                .build();

        return new WithdrawTestResult(withdrawStub.withdrawFunds(withdrawRequest));
    }

    private BalanceTestResult getBalance(int userId) {
        Balance.BalanceRequest request = Balance.BalanceRequest.newBuilder().setUserId(userId).build();
        return new BalanceTestResult(balanceStub.getBalance(request));
    }

    private RegisterTestResult register(int userId) {
        Register.RegisterRequest request = Register.RegisterRequest.newBuilder().setUserId(userId).build();
        return new RegisterTestResult(registerStub.register(request));
    }

}

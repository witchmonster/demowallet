package com.jkramr.demowalletserver;

import com.jkramr.demowalletapi.grpc.*;
import com.jkramr.demowalletapi.model.Common;
import com.jkramr.demowalletserver.repository.WalletRepository;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(properties = {"grpc.client.wallet.address=static://localhost:9090", "grpc.client.wallet.negotiationType=PLAINTEXT"})
public class DemowalletServerApplicationTests {

    @GrpcClient("wallet")
    private RegisterServiceGrpc.RegisterServiceBlockingStub registerStub;

    @GrpcClient("wallet")
    private BalanceServiceGrpc.BalanceServiceBlockingStub balanceStub;

    @GrpcClient("wallet")
    private DepositServiceGrpc.DepositServiceBlockingStub depositStub;

    @GrpcClient("wallet")
    private WithdrawServiceGrpc.WithdrawServiceBlockingStub withdrawStub;

    @Autowired
    WalletRepository walletRepository;

    @Before
    public void setUp() {
        //given there's no registration method described in the Task, I assume users are registered prior to any task execution
        walletRepository.deleteAll();
        registerStub.register(Register.RegisterRequest.newBuilder().setUserId(1).build());
        registerStub.register(Register.RegisterRequest.newBuilder().setUserId(2).build());
    }

    @Test
    public void contextLoads() {
    }

    @Test
    public void taskIntegrationTest() {
        withdrawFunds(1, Common.Currency.USD, 200).assertInsufficientFunds();
        depositFunds(1, Common.Currency.USD, 100).assertSuccessful();
        getBalance(1)
                .forCurrency(Common.Currency.USD)
                .amountEquals(100)
                .assertBalances();
        withdrawFunds(1, Common.Currency.USD, 200).assertInsufficientFunds();
        depositFunds(1, Common.Currency.EUR, 100).assertSuccessful();
        getBalance(1)
                .forCurrency(Common.Currency.USD).amountEquals(100)
                .forCurrency(Common.Currency.EUR).amountEquals(100)
                .assertBalances();
        withdrawFunds(1, Common.Currency.USD, 200).assertInsufficientFunds();
        depositFunds(1, Common.Currency.USD, 100).assertSuccessful();
        withdrawFunds(1, Common.Currency.USD, 200).assertSuccessful();
        getBalance(1)
                .forCurrency(Common.Currency.USD).amountEquals(0)
                .forCurrency(Common.Currency.EUR).amountEquals(100)
                .assertBalances();
        withdrawFunds(1, Common.Currency.USD, 200).assertInsufficientFunds();
    }

    @Test
    public void givenNoWalletGetBalanceShouldReturnEmptyResult() {
        getBalance(1)
                .forCurrency(Common.Currency.USD).amountEquals(0)
                .forCurrency(Common.Currency.EUR).amountEquals(0)
                .forCurrency(Common.Currency.GBP).amountEquals(0)
                .assertBalances();
    }

    @Test
    public void givenNoWalletShouldCreateDepositAndWithdrawSuccessfully() {
        depositFunds(1, Common.Currency.EUR, 100).assertSuccessful();
        getBalance(1).forCurrency(Common.Currency.EUR).amountEquals(100).assertBalances();
        withdrawFunds(1, Common.Currency.EUR, 50).assertSuccessful();
        getBalance(1).forCurrency(Common.Currency.EUR).amountEquals(50).assertBalances();
    }

    private DepositTestResult depositFunds(int userId, Common.Currency currency, double amount) {
        Deposit.DepositRequest depositRequest = Deposit.DepositRequest.newBuilder()
                .setUserId(userId)
                .setAmount(amount)
                .setCurrency(currency)
                .build();
        return new DepositTestResult(depositStub.depositFunds(depositRequest));
    }

    private WithdrawTestResult withdrawFunds(int userId, Common.Currency currency, int amount) {
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

}

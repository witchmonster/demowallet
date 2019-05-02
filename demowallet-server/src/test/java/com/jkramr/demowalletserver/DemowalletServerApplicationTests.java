package com.jkramr.demowalletserver;

import com.jkramr.demowalletapi.grpc.*;
import com.jkramr.demowalletapi.model.Common;
import com.jkramr.demowalletserver.model.User;
import com.jkramr.demowalletserver.repository.UserRepository;
import com.jkramr.demowalletserver.repository.WalletRepository;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.junit.Assert;
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
    BalanceServiceGrpc.BalanceServiceBlockingStub balanceStub;

    @GrpcClient("wallet")
    DepositServiceGrpc.DepositServiceBlockingStub depositStub;

    @GrpcClient("wallet")
    WithdrawServiceGrpc.WithdrawServiceBlockingStub withdrawStub;

    @Autowired
    UserRepository userRepository;

    @Autowired
    WalletRepository walletRepository;

    @Before
    public void setUp() throws Exception {
        //given there's no registration method described in the task, I assume numberOfUsers are already registered and exist in db
        userRepository.save(new User(1));
        userRepository.save(new User(2));
        walletRepository.deleteAll();
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
        Balance.BalanceResponse balance = balanceStub.getBalance(Balance.BalanceRequest.newBuilder().setUserId(1).build());
        Assert.assertEquals(Balance.BalanceResponse.newBuilder().build(), balance);
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

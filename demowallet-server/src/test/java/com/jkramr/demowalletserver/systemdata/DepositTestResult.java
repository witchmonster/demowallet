package com.jkramr.demowalletserver.systemdata;

import com.jkramr.demowalletapi.grpc.Deposit;
import org.junit.Assert;

public class DepositTestResult {
    private Deposit.DepositResponse response;

    public DepositTestResult(Deposit.DepositResponse response) {
        this.response = response;
    }

    public void assertSuccessful() {
        Assert.assertEquals(Deposit.DepositResponse.ResponseCode.OK, response.getCode());
    }

    public void assertWalletNotFound() {
        Assert.assertEquals(Deposit.DepositResponse.ResponseCode.WALLET_NOT_FOUND, response.getCode());
    }

}

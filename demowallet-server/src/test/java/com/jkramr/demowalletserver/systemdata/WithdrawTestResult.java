package com.jkramr.demowalletserver.systemdata;

import com.jkramr.demowalletapi.grpc.Withdraw;
import org.junit.Assert;

public class WithdrawTestResult {
    private Withdraw.WithdrawResponse response;

    public WithdrawTestResult(Withdraw.WithdrawResponse response) {
        this.response = response;
    }

    public void assertSuccessful() {
        Assert.assertEquals(Withdraw.WithdrawResponse.ResponseCode.OK, response.getCode());
    }

    public void assertInsufficientFunds() {
        Assert.assertEquals(Withdraw.WithdrawResponse.ResponseCode.INSUFFICIENT_FUNDS, response.getCode());
    }

    public void assertWalletNotFound() {
        Assert.assertEquals(Withdraw.WithdrawResponse.ResponseCode.WALLET_NOT_FOUND, response.getCode());
    }

    public void assertInvalidRequest() {
        Assert.assertEquals(Withdraw.WithdrawResponse.ResponseCode.INVALID_REQUEST, response.getCode());
    }

    public void assertUnknownCurrency() {
        Assert.assertEquals(Withdraw.WithdrawResponse.ResponseCode.UNKNOWN_CURRENCY, response.getCode());
    }
}

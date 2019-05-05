package com.jkramr.demowalletserver;

import com.jkramr.demowalletapi.grpc.Withdraw;
import org.junit.Assert;

class WithdrawTestResult {
    private Withdraw.WithdrawResponse response;

    WithdrawTestResult(Withdraw.WithdrawResponse response) {
        this.response = response;
    }

    void assertSuccessful() {
        Assert.assertEquals("Ok", response.getDebugMessage());
    }

    void assertInsufficientFunds() {
        Assert.assertEquals(Withdraw.WithdrawResponse.Error.INSUFFICIENT_FUNDS, response.getError());
    }
}

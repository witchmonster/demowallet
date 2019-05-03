package com.jkramr.demowalletserver;

import com.jkramr.demowalletapi.grpc.Deposit;
import org.junit.Assert;

class DepositTestResult {
    private Deposit.DepositResponse response;

    DepositTestResult(Deposit.DepositResponse response) {
        this.response = response;
    }

    void assertSuccessful() {
        Assert.assertEquals("ok", response.getDebugMessage());
    }
}

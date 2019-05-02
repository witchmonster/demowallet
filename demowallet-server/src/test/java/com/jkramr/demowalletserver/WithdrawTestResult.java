package com.jkramr.demowalletserver;

import com.jkramr.demowalletapi.grpc.Withdraw;
import lombok.Data;
import org.junit.Assert;

@Data
class WithdrawTestResult {
    private Withdraw.WithdrawResponse response;

    WithdrawTestResult(Withdraw.WithdrawResponse response) {
        this.response = response;
    }

    void assertSuccessful() {
        Assert.assertEquals("ok", response.getDebugMessage());
    }

    void assertInsufficientFunds() {
        Assert.assertEquals(Withdraw.WithdrawResponse.Error.INSUFFICIENT_FUNDS, response.getError());
    }
}

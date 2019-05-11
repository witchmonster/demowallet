package com.jkramr.demowalletserver.systemdata;

import com.jkramr.demowalletapi.grpc.Register;
import org.junit.Assert;

public class RegisterTestResult {
    private Register.RegisterResponse response;

    public RegisterTestResult(Register.RegisterResponse response) {
        this.response = response;
    }

    public void assertSuccessful() {
        Assert.assertEquals(Register.RegisterResponse.ResponseCode.OK, response.getCode());
    }

    public void assertInvalidRequest() {
        Assert.assertEquals(Register.RegisterResponse.ResponseCode.INVALID_REQUEST, response.getCode());
    }
}

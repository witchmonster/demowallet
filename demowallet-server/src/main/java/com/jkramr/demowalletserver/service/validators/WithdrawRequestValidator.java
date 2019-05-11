package com.jkramr.demowalletserver.service.validators;

import com.jkramr.demowalletapi.grpc.Withdraw;
import org.springframework.stereotype.Component;


@Component
public class WithdrawRequestValidator extends CommonRequestValidator<Withdraw.WithdrawRequest, Withdraw.WithdrawResponse> {
    @Override
    protected Withdraw.WithdrawResponse getResponseOnInvalidRequest(Withdraw.WithdrawRequest request) {
        Withdraw.WithdrawResponse.Builder response = Withdraw.WithdrawResponse.newBuilder();

        if (!isValidUserId(request.getUserId()) || !isValidAmount(request.getAmount())) {
            return response.setCode(Withdraw.WithdrawResponse.ResponseCode.INVALID_REQUEST).build();
        }

        return requestIsValid();
    }
}
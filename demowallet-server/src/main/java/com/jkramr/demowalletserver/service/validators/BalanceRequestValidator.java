package com.jkramr.demowalletserver.service.validators;

import com.jkramr.demowalletapi.grpc.Balance;
import org.springframework.stereotype.Component;

@Component
public class BalanceRequestValidator extends CommonRequestValidator<Balance.BalanceRequest, Balance.BalanceResponse> {

    @Override
    protected Balance.BalanceResponse getResponseOnInvalidRequest(Balance.BalanceRequest request) {
        if (!isValidUserId(request.getUserId())) {
            return Balance.BalanceResponse.newBuilder().setCode(Balance.BalanceResponse.ResponseCode.INVALID_REQUEST).build();
        }

        return requestIsValid();
    }

}

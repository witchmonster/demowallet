package com.jkramr.demowalletserver.service.validators;

import com.jkramr.demowalletapi.grpc.Deposit;
import org.springframework.stereotype.Component;

import static com.jkramr.demowalletapi.grpc.Deposit.DepositResponse.newBuilder;

@Component
public class DepositRequestValidator extends CommonRequestValidator<Deposit.DepositRequest, Deposit.DepositResponse> {

    @Override
    protected Deposit.DepositResponse getResponseOnInvalidRequest(Deposit.DepositRequest request) {
        Deposit.DepositResponse.Builder response = newBuilder();

        if (!isValidUserId(request.getUserId()) || !isValidAmount(request.getAmount())) {
            return response.setCode(Deposit.DepositResponse.ResponseCode.INVALID_REQUEST).build();
        }

        return requestIsValid();
    }

}
package com.jkramr.demowalletserver.service.validators;

import com.jkramr.demowalletapi.grpc.Register;
import org.springframework.stereotype.Component;

@Component
public class RegisterRequestValidator extends CommonRequestValidator<Register.RegisterRequest, Register.RegisterResponse> {
    @Override
    protected Register.RegisterResponse getResponseOnInvalidRequest(Register.RegisterRequest request) {
        Register.RegisterResponse.Builder response = Register.RegisterResponse.newBuilder();

        if (!isValidUserId(request.getUserId())) {
            return response.setCode(Register.RegisterResponse.ResponseCode.INVALID_REQUEST).build();
        }

        return requestIsValid();
    }
}

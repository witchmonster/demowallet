package com.jkramr.demowalletserver.service.validators;

import com.google.protobuf.GeneratedMessageV3;

public abstract class CommonRequestValidator<T extends GeneratedMessageV3, V extends GeneratedMessageV3> extends AbstractRequestValidator<T, V> {

    boolean isValidAmount(double amount) {
        return amount >= 0;
    }

    boolean isValidUserId(int userId) {
        return userId > 0;
    }
}

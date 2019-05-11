package com.jkramr.demowalletserver.service.validators;

import com.google.protobuf.GeneratedMessageV3;

import java.util.Optional;

public abstract class AbstractRequestValidator<T extends GeneratedMessageV3, V extends GeneratedMessageV3> implements RequestValidator<T, V> {
    public Optional<V> onInvalidRequest(T request) {
        return Optional.ofNullable(getResponseOnInvalidRequest(request));
    }

    protected abstract V getResponseOnInvalidRequest(T request);

    V requestIsValid() {
        return null;
    }
}

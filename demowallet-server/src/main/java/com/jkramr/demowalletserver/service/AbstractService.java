package com.jkramr.demowalletserver.service;

import com.google.protobuf.GeneratedMessageV3;
import com.jkramr.demowalletserver.service.validators.RequestValidator;

public abstract class AbstractService<T extends GeneratedMessageV3, V extends GeneratedMessageV3> implements Service<T, V>{

    protected final RequestValidator<T, V> requestValidator;

    public AbstractService(RequestValidator<T, V> requestValidator) {
        this.requestValidator = requestValidator;
    }

    @Override
    public V processRequest(T request) {
        return requestValidator.onInvalidRequest(request).orElseGet(() -> doProcess(request));
    }

    protected abstract V doProcess(T request);
}

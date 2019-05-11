package com.jkramr.demowalletserver.service.validators;

import com.google.protobuf.GeneratedMessageV3;

import java.util.Optional;

public interface RequestValidator<U extends GeneratedMessageV3, W extends GeneratedMessageV3> {
    Optional<W> onInvalidRequest(U request);
}

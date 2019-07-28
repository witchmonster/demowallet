package com.betpawa.common.protomapper.model;

import com.betpawa.common.protomapper.AnyProtoType;
import com.betpawa.common.protomapper.Proto3Entity;

@Proto3Entity(value = AnyProtoType.class)
public class ConstructorThrowsException {
    public ConstructorThrowsException() {
        throw new UnsupportedOperationException("not implemented");
    }
}

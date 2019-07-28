package com.betpawa.common.protomapper;

import com.google.protobuf.Message;

public interface Mapper {
    <J, P extends Message> void mapFields(Class<?> javaClass, P protoObj, J javaObj);
}

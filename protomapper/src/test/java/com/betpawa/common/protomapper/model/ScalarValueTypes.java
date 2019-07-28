package com.betpawa.common.protomapper.model;

import com.betpawa.common.protomapper.Proto3Entity;
import com.betpawa.common.protomapper.ScalarValueTypesProto;
import com.google.protobuf.ByteString;

import lombok.Data;

/**
 * Scalar value types according to proto3 documentation
 * https://developers.google.com/protocol-buffers/docs/proto3#scalar
 */
@Data
@Proto3Entity(value = ScalarValueTypesProto.class)
public class ScalarValueTypes {
    private double doubleValue;
    private float floatValue;
    private int int32Value;
    private long int64Value;
    private int uint32Value;
    private long uint64Value;
    private int sint32Value;
    private long sint64Value;
    private int fixed32Value;
    private long fixed64Value;
    private int sfixed32Value;
    private long sfixed64Value;
    private boolean boolValue;
    private String stringValue;
    private ByteString bytesValue;
}

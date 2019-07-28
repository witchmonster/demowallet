package com.betpawa.common.protomapper.model;

import com.betpawa.common.protomapper.Proto3Entity;
import com.betpawa.common.protomapper.ScalarValueTypesAutoboxingProto;
import com.google.protobuf.ByteString;

import lombok.Data;

/**
 * Autoboxed verion of ScalarValueTypes
 * Same fields used as in ScalarValueTypes but mapped to their autoboxed Java counterparts
 * @see ScalarValueTypes
 */
@Data
@Proto3Entity(value = ScalarValueTypesAutoboxingProto.class)
public class ScalarValueTypesAutoboxing {
    private Double DoubleValue;
    private Float FloatValue;
    private Integer Integer32Value;
    private Long Integer64Value;
    private Integer uInteger32Value;
    private Long uInteger64Value;
    private Integer sInteger32Value;
    private Long sInteger64Value;
    private Integer fixed32Value;
    private Long fixed64Value;
    private Integer sfixed32Value;
    private Long sfixed64Value;
    private Boolean boolValue;
    private String stringValue;
    private ByteString bytesValue;
}

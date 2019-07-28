package com.betpawa.common.protomapper.model;

import com.betpawa.common.protomapper.NestedFieldTypesProto;
import com.betpawa.common.protomapper.Proto3Entity;

import lombok.Data;

@Data
@Proto3Entity(value = NestedFieldTypesProto.class)
public class NestedFieldTypes {
    private int field1;
    private NestedMessageLevel1 field2;
}

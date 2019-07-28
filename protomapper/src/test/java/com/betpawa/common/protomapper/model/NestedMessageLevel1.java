package com.betpawa.common.protomapper.model;

import com.betpawa.common.protomapper.NestedMessageLevel1Proto;
import com.betpawa.common.protomapper.Proto3Entity;

import lombok.Data;

@Data
@Proto3Entity(value = NestedMessageLevel1Proto.class)
public class NestedMessageLevel1 {
    private int field1;
    private NestedMessageLevel2 field2;
}

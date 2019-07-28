package com.betpawa.common.protomapper.model;

import com.betpawa.common.protomapper.NestedMessageLevel2Proto;
import com.betpawa.common.protomapper.Proto3Entity;

import lombok.Data;

@Data
@Proto3Entity(value = NestedMessageLevel2Proto.class)
public class NestedMessageLevel2 {
    private int field1;
    private int field2;
}

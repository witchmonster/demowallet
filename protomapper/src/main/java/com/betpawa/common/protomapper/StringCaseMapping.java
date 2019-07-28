package com.betpawa.common.protomapper;

import com.google.common.base.CaseFormat;

import lombok.Value;

@Value
public class StringCaseMapping {
    private final String value;
    private final CaseFormat caseFormat;
}

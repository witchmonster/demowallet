package com.betpawa.common.protomapper.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Predicate;

import com.betpawa.common.protomapper.StringCaseMapping;
import com.google.common.base.CaseFormat;

public class StringUtils {
    private static List<BiFunction<String, CaseFormat, StringCaseMapping>> supportedToCaseConverters;
    private static List<BiFunction<String, CaseFormat, StringCaseMapping>> supportedFromCaseConverters;
    
    static {
        supportedToCaseConverters = new ArrayList<>();
        supportedToCaseConverters.add(StringCaseMapping::new);
        supportedToCaseConverters.add((name, caseFormat) -> new StringCaseMapping(CaseFormat.LOWER_UNDERSCORE.to(caseFormat, name), CaseFormat.LOWER_UNDERSCORE));
        supportedToCaseConverters.add((name, caseFormat) -> new StringCaseMapping(CaseFormat.UPPER_UNDERSCORE.to(caseFormat, name), CaseFormat.UPPER_UNDERSCORE));
        supportedToCaseConverters.add((name, caseFormat) -> new StringCaseMapping(CaseFormat.UPPER_CAMEL.to(caseFormat, name), CaseFormat.UPPER_CAMEL));
    
        supportedFromCaseConverters = new ArrayList<>();
        supportedFromCaseConverters.add(StringCaseMapping::new);
        supportedFromCaseConverters.add((name, caseFormat) -> new StringCaseMapping(caseFormat.to(CaseFormat.LOWER_UNDERSCORE, name), CaseFormat.LOWER_UNDERSCORE));
        supportedFromCaseConverters.add((name, caseFormat) -> new StringCaseMapping(caseFormat.to(CaseFormat.UPPER_UNDERSCORE, name), CaseFormat.UPPER_UNDERSCORE));
        supportedFromCaseConverters.add((name, caseFormat) -> new StringCaseMapping(caseFormat.to(CaseFormat.UPPER_CAMEL, name), CaseFormat.UPPER_CAMEL));
    }
    
    public static StringCaseMapping getMatchToCase(final CaseFormat caseFormat, final String protoFieldName, final Predicate<String> predicate) {
        for (BiFunction<String, CaseFormat, StringCaseMapping> converter: supportedToCaseConverters){
            StringCaseMapping fieldName = converter.apply(protoFieldName, caseFormat);
            if (predicate.test(fieldName.getValue())) {
                return fieldName;
            }
        }
        
        return null;
    }
    
    public static StringCaseMapping getMatchFromCase(final CaseFormat caseFormat, final String protoFieldName, final Predicate<String> predicate) {
        for (BiFunction<String, CaseFormat, StringCaseMapping> converter: supportedFromCaseConverters){
            StringCaseMapping fieldName = converter.apply(protoFieldName, caseFormat);
            if (predicate.test(fieldName.getValue())) {
                return fieldName;
            }
        }
        
        return null;
    }
    
}

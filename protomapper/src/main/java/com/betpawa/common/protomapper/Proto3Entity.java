package com.betpawa.common.protomapper;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.google.protobuf.Message;

@Target(value = ElementType.TYPE)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface Proto3Entity {
    
    /**
     * Indicates protobuf class mapping for this entity
     *
     * @return Protobuf generated class type
     */
    Class<? extends Message> value();
    
    /**
     * Indicates the field mapping strategy
     *
     * @return Mapping strategy for annotated type
     */
    Proto3Strategy strategy() default Proto3Strategy.DEFAULT;
}

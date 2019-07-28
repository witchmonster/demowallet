package com.betpawa.common.protomapper;

import com.betpawa.common.protomapper.model.ConstructorNotPublic;
import com.betpawa.common.protomapper.model.ConstructorThrowsException;
import com.betpawa.common.protomapper.model.NestedFieldTypes;
import com.betpawa.common.protomapper.model.NestedMessageLevel1;
import com.betpawa.common.protomapper.model.NoDefaultConstructor;
import com.betpawa.common.protomapper.model.NoProtomapperAnnotation;
import com.betpawa.common.protomapper.model.ScalarValueTypes;
import com.betpawa.common.protomapper.model.WrongProtoClassInAnnotation;
import com.google.protobuf.ByteString;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class Proto3AnnotationMapperTest {
    
    private Proto3AnnotationMapper proto3AnnotationMapper;
    
    @BeforeEach
    void setUp() {
        proto3AnnotationMapper = new Proto3AnnotationMapper();
    }
    
    @Test
    void primitiveTestFromProto() {
        double doubleValue = 1.0;
        float floatValue = 2.0F;
        int int32Value = 3;
        long int64Value = 4L;
        int uint32Value = 5;
        long uint64Value = 6;
        int sint32Value = 7;
        long sint64Value = 8;
        int fixed32Value = 9;
        long fixed64Value = 10;
        int sfixed32Value = 11;
        long sfixed64Value = 12;
        boolean boolValue = true;
        String stringValue = "14";
        ByteString bytesValue = ByteString.copyFromUtf8("15");
        
        ScalarValueTypesProto protoObj = ScalarValueTypesProto.newBuilder()
                                                              .setDoubleValue(doubleValue)
                                                              .setFloatValue(floatValue)
                                                              .setInt32Value(int32Value)
                                                              .setInt64Value(int64Value)
                                                              .setUint32Value(uint32Value)
                                                              .setUint64Value(uint64Value)
                                                              .setSint32Value(sint32Value)
                                                              .setSint64Value(sint64Value)
                                                              .setFixed32Value(fixed32Value)
                                                              .setFixed64Value(fixed64Value)
                                                              .setSfixed32Value(sfixed32Value)
                                                              .setSfixed64Value(sfixed64Value)
                                                              .setBoolValue(boolValue)
                                                              .setStringValue(stringValue)
                                                              .setBytesValue(bytesValue)
                                                              .build();
        ScalarValueTypes javaObj = proto3AnnotationMapper.map(ScalarValueTypes.class, protoObj);
        
        assertEquals(doubleValue, javaObj.getDoubleValue());
        assertEquals(floatValue, javaObj.getFloatValue());
        assertEquals(int32Value, javaObj.getInt32Value());
        assertEquals(int64Value, javaObj.getInt64Value());
        assertEquals(uint32Value, javaObj.getUint32Value());
        assertEquals(uint64Value, javaObj.getUint64Value());
        assertEquals(sint32Value, javaObj.getSint32Value());
        assertEquals(sint64Value, javaObj.getSint64Value());
        assertEquals(fixed32Value, javaObj.getFixed32Value());
        assertEquals(fixed64Value, javaObj.getFixed64Value());
        assertEquals(sfixed32Value, javaObj.getSfixed32Value());
        assertEquals(sfixed64Value, javaObj.getSfixed64Value());
        assertEquals(boolValue, javaObj.isBoolValue());
        assertEquals(stringValue, javaObj.getStringValue());
        assertEquals(bytesValue, javaObj.getBytesValue());
    }
    
    @Test
    void nestedTypesTestFromProto() {
        final int field1 = 1;
        final int field2 = 2;
        
        NestedMessageLevel2Proto level2Proto = NestedMessageLevel2Proto.newBuilder()
                                                                 .setField1(field1)
                                                                 .setField2(field2)
                                                                 .build();
        NestedMessageLevel1Proto level1Proto = NestedMessageLevel1Proto.newBuilder()
                                                                 .setField1(field1)
                                                                 .setField2(level2Proto)
                                                                 .build();
        NestedFieldTypesProto protoObj = NestedFieldTypesProto.newBuilder()
                                                              .setField1(field1)
                                                              .setField2(level1Proto)
                                                              .build();
        
        NestedFieldTypes javaObj = proto3AnnotationMapper.map(NestedFieldTypes.class, protoObj);
        
        assertEquals(field1, javaObj.getField1());
        assertEquals(field1, javaObj.getField2().getField1());
        assertEquals(field1, javaObj.getField2().getField2().getField1());
        assertEquals(field2, javaObj.getField2().getField2().getField2());
    }
    
    @Test
    void givenWrongProtoClassInAnnotationShouldThrowException() {
        Executable shouldThrow = () -> proto3AnnotationMapper.map(WrongProtoClassInAnnotation.class, AnyProtoType.newBuilder().build());
        assertThrows(Proto3MapperException.class, shouldThrow);
        try {
            shouldThrow.execute();
        } catch (Throwable e) {
            assertEquals(String.format(Proto3AnnotationMapper.ANNOTATION_DOES_NOT_MATCH_THE_DESTINATION_CLASS_MESSAGE,
                                       WrongProtoType.class.getName(),
                                       AnyProtoType.class.getName(),
                                       WrongProtoClassInAnnotation.class.getName()),
                         e.getMessage());
        }
    }
    
    @Test
    void givenAnnotationMissingShouldThrowException() {
        Executable shouldThrow = () -> proto3AnnotationMapper.map(NoProtomapperAnnotation.class, AnyProtoType.newBuilder().build());
        assertThrows(Proto3MapperException.class, shouldThrow);
        try {
            shouldThrow.execute();
        } catch (Throwable e) {
            assertEquals(String.format(Proto3AnnotationMapper.ANNOTATION_MISSING_FOR_CLASS_MESSAGE, NoProtomapperAnnotation.class.getName()), e.getMessage());
        }
    }
    
    @Test
    void givenNoDefaultConstructorShouldThrowException() {
        Executable shouldThrow = () -> proto3AnnotationMapper.map(NoDefaultConstructor.class, AnyProtoType.newBuilder().build());
        assertThrows(Proto3MapperException.class, shouldThrow);
        try {
            shouldThrow.execute();
        } catch (Throwable e) {
            assertEquals(String.format(Proto3AnnotationMapper.DEFAULT_CONSTRUCTOR_NOT_FOUND_FOR_CLASS_MESSAGE, NoDefaultConstructor.class.getName()), e.getMessage());
        }
    }
    
    @Test
    void givenConstructorNotPublicShouldThrowException() {
        Executable shouldThrow = () -> proto3AnnotationMapper.map(ConstructorNotPublic.class, AnyProtoType.newBuilder().build());
        assertThrows(Proto3MapperException.class, shouldThrow);
        try {
            shouldThrow.execute();
        } catch (Throwable e) {
            assertEquals(String.format(Proto3AnnotationMapper.CONSTRUCTOR_NOT_PUBLIC_FOR_CLASS_MESSAGE, ConstructorNotPublic.class.getName()), e.getMessage());
        }
    }
    
    @Test
    void givenConstructorThrowsExceptionShouldThrowException() {
        Executable shouldThrow = () -> proto3AnnotationMapper.map(ConstructorThrowsException.class, AnyProtoType.newBuilder().build());
        assertThrows(Proto3MapperException.class, shouldThrow);
        try {
            shouldThrow.execute();
        } catch (Throwable e) {
            assertEquals(String.format(Proto3AnnotationMapper.COULDNT_CREATE_AN_INSTANCE_OF_CLASS_MESSAGE, ConstructorThrowsException.class.getName()), e.getMessage());
        }
    }
}
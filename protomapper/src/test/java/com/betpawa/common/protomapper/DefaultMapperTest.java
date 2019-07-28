package com.betpawa.common.protomapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.betpawa.common.protomapper.model.CompatibleFields;
import com.betpawa.common.protomapper.model.CompatibleFieldsMixedCase;
import com.betpawa.common.protomapper.model.FinalField;
import com.betpawa.common.protomapper.model.StaticField;
import com.betpawa.common.protomapper.model.IncompatibleFields;
import com.betpawa.common.protomapper.model.Level2Subclass;
import com.betpawa.common.protomapper.model.Level1Subclass;
import com.betpawa.common.protomapper.model.Level0Class;
import com.betpawa.common.protomapper.model.TooManyFields;

@ExtendWith(MockitoExtension.class)
class DefaultMapperTest {
    
    @Mock
    private Proto3AnnotationMapper proto3Mapper;
    
    private DefaultMapper mapper;
    
    @BeforeEach
    void setUp() {
        mapper = new DefaultMapper(proto3Mapper);
    }
    
    @Test
    void givenObjectClassShouldReturnEmptyList() {
        Map<String, Field> allInheritedFields = mapper.getAllFields(Object.class);
        assertEquals(allInheritedFields, Collections.emptyMap());
    }
    
    @Test
    void givenInterfaceShouldReturnEmptyList() {
        Map<String, Field> allInheritedFields = mapper.getAllFields(Map.class);
        assertEquals(allInheritedFields, Collections.emptyMap());
    }
    
    @Test
    void givenSimpleClassShouldReturnDeclaredFields() {
        Set<String> fieldNames = mapper.getAllFields(Level0Class.class).keySet();
        assertEquals(new HashSet<>(Collections.singletonList("level0Field")), fieldNames);
    }
    
    @Test
    void givenInheritedClassShouldReturnAllFields() {
        Set<String> fieldNames = mapper.getAllFields(Level1Subclass.class).keySet();
        assertEquals(new HashSet<>(Arrays.asList("level1Field", "level0Field")), fieldNames);
    }
    
    @Test
    void givenLevel2InheritanceClassShouldReturnAllFields() {
        Set<String> fieldNames = mapper.getAllFields(Level2Subclass.class).keySet();
        assertEquals(new HashSet<>(Arrays.asList("level2Field", "level1Field", "level0Field")), fieldNames);
    }
    
    @Test
    void givenIncompatibleClassesShouldThrowException() {
        Executable executable = () -> mapper.mapFields(TooManyFields.class, MissingFieldsType.newBuilder().build(), new TooManyFields());
        assertThrows(Proto3MapperException.class, executable);
        try {
            executable.execute();
        } catch (Throwable throwable) {
            assertEquals(String.format(DefaultMapper.INCOMPATIBLE_CLASS_BINDING_MISSING_FIELDS_MESSAGE, Collections.singletonList("field3").toString()), throwable.getMessage());
        }
    }
    
    @Test
    void givenIncompatibleFiledTypesShouldThrowException() {
        Executable executable = () -> mapper.mapFields(IncompatibleFields.class, IncompatibleFieldsProto.newBuilder().build(), new IncompatibleFields());
        assertThrows(Proto3MapperException.class, executable);
        try {
            executable.execute();
        } catch (Throwable throwable) {
            assertEquals(String.format(DefaultMapper.INCOMPATIBLE_CLASS_BINDING_INCOMPATIBLE_FIELD_TYPE_MESSAGE, "field2", "field2_"), throwable.getMessage());
        }
    }
    
    @Test
    void givenCompatibleClassesShouldMap() {
        CompatibleFields javaObj = new CompatibleFields();
        int field1 = 1;
        int field2 = 2;
        mapper.mapFields(CompatibleFields.class, CompatibleFieldsProto.newBuilder().setField1(field1).setField2(field2).build(), javaObj);
        assertEquals(field1, javaObj.getField1());
        assertEquals(field2, javaObj.getField2());
    }
    
    @Test
    void givenFinalFieldShouldMap() {
        int field1 = 1;
        int field2 = 2;
        FinalField javaObj = new FinalField(0);
        mapper.mapFields(FinalField.class, FinalFieldProto.newBuilder().setField1(field1).setField2(field2).build(), javaObj);
        assertEquals(field1, javaObj.getField1());
        assertEquals(field2, javaObj.getField2());
    }
    
    @Test
    void givenStaticFieldShouldMapAllNonStatic() {
        StaticField javaObj = new StaticField();
        int field2 = 2;
        mapper.mapFields(StaticField.class, StaticFieldProto.newBuilder().setField1(1).setField2(field2).build(), javaObj);
        assertEquals(field2, javaObj.getField2());
    }
    
    @Test
    void givenCompatibleMixedCaseClassesShouldMap() {
        CompatibleFieldsMixedCase javaObj = new CompatibleFieldsMixedCase();
        int field1 = 1;
        int field2 = 2;
        int field3 = 3;
        mapper.mapFields(CompatibleFieldsMixedCase.class, CompatibleFieldsProtoMixedCase.newBuilder()
                                                                                        .setFieldCase1(field1)
                                                                                        .setFieldCase2(field2)
                                                                                        .setFieldCase3(field3)
                                                                                        .build(), javaObj);
        assertEquals(field1, javaObj.getFieldCase1());
        assertEquals(field2, javaObj.getFieldCase2());
        assertEquals(field3, javaObj.getFieldCase3());
    }
}
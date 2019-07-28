package com.betpawa.common.protomapper;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.betpawa.common.protomapper.utils.StringUtils;
import com.google.common.base.CaseFormat;
import com.google.protobuf.Descriptors;
import com.google.protobuf.Message;

import lombok.extern.slf4j.Slf4j;

/**
 * This mapper has a left binding: it will map all the destination fields and ignore any unmapped proto fields.
 */
@Slf4j
public class DefaultMapper implements Mapper {
    
    static final CaseFormat JAVA_CLASS_CASE_FORMAT = CaseFormat.LOWER_CAMEL;
    
    static final String INCOMPATIBLE_CLASS_BINDING_MISSING_FIELDS_MESSAGE = "Incompatible class binding. Fields %s missing in destination class";
    static final String INCOMPATIBLE_CLASS_BINDING_INCOMPATIBLE_FIELD_TYPE_MESSAGE = "Incompatible class binding. Destination field type %s is incompatible with proto field type %s";
    static final String FIELD_INACCESSIBLE_MESSAGE = "Field inaccessible (static): %s";
    static final String PROTO_GETTER_PREFIX = "get";
    static final String COULD_NOT_RESOLVE_FIELD_MESSAGE = "Could not resolve field: %s";
    
    private final Proto3AnnotationMapper annotationMapper;
    
    public DefaultMapper(final Proto3AnnotationMapper annotationMapper) {this.annotationMapper = annotationMapper;}
    
    Map<String, Field> getAllFields(final Class<?> javaClass) {
        Map<String, Field> fields = new HashMap<>();
        
        Class<?> parentClass = javaClass;
        
        while (parentClass != null) {
            fields.putAll(Arrays.stream(parentClass.getDeclaredFields())
                                .collect(Collectors.toMap(Field::getName, field -> field)));
            parentClass = parentClass.getSuperclass();
        }
        
        return fields;
    }
    
    @Override
    public <J, P extends Message> void mapFields(final Class<?> javaClass, final P protoObj, final J javaObj) {
        Map<String, Field> fields = getAllFields(javaClass);
        Map<String, String> protoMapping = getProtoFieldMapping(protoObj, fields);
        
        protoObj.getAllFields().forEach((descriptor, value) -> mapField(javaObj, fields, protoMapping, value, descriptor));
    }
    
    private <J> void mapField(final J javaObj,
                              final Map<String, Field> fields,
                              final Map<String, String> mapping,
                              final Object value,
                              final Descriptors.FieldDescriptor protoField) {
        StringCaseMapping matchingProtoFieldName = StringUtils.getMatchToCase(JAVA_CLASS_CASE_FORMAT, protoField.getJsonName(), (name) -> mapping.get(name) != null);
        
        if (matchingProtoFieldName == null) {
            throw new Proto3MapperException(String.format(COULD_NOT_RESOLVE_FIELD_MESSAGE, protoField.getJsonName()));
        }
        
        Field javaField = fields.get(matchingProtoFieldName.getValue());
        filterByFieldType(
                protoField.getJavaType(), this::mapEnumField,
                () -> mapCompositeField(javaField.getType(), javaObj, (Message) value, javaField),
                () -> mapScalarField(javaObj, value, javaField));
    }
    
    private <J> J mapEnumField() {
        throw new UnsupportedOperationException("not implemented");
    }
    
    private <T> T filterByFieldType(final Descriptors.FieldDescriptor.JavaType javaType, final Supplier<T> onEnum, final Supplier<T> onMessage, final Supplier<T> onScalar) {
        switch (javaType) {
            case ENUM:
                return onEnum.get();
            case MESSAGE:
                return onMessage.get();
            case INT:
            case LONG:
            case FLOAT:
            case DOUBLE:
            case STRING:
            case BOOLEAN:
            case BYTE_STRING:
            default:
                return onScalar.get();
        }
    }
    
    private <J, P extends Message> J mapCompositeField(final Class<?> type, final J javaObj, final P protoObj, final Field field) {
        J value = annotationMapper.map(type, protoObj);
        return mapScalarField(javaObj, value, field);
    }
    
    private <J> J mapScalarField(final J javaObj, final Object value, final Field field) {
        field.setAccessible(true);
        try {
            field.set(javaObj, value);
        } catch (IllegalAccessException e) {
            throw new Proto3MapperException(String.format(FIELD_INACCESSIBLE_MESSAGE, field));
        }
        return javaObj;
    }
    
    private <P extends Message> Map<String, String> getProtoFieldMapping(final P protoObj, final Map<String, Field> fields) {
        Map<String, Field> allProtoFields = getAllFields(protoObj.getClass());
        Map<String, String> mapping = new HashMap<>();
        
        final List<String> missingFields = new ArrayList<>();
        for (String fieldName : fields.keySet()) {
            StringCaseMapping matchToCamelCase = StringUtils.getMatchToCase(JAVA_CLASS_CASE_FORMAT, fieldName,
                                                                              (name) -> allProtoFields.containsKey(withUnderscore(name)));
            if (matchToCamelCase != null) {
                Field field = fields.get(fieldName);
                Field protoField = allProtoFields.get(withUnderscore(matchToCamelCase.getValue()));
                Class<?> toType = field.getType();
                StringCaseMapping matchToDescriptor = StringUtils.getMatchFromCase(JAVA_CLASS_CASE_FORMAT, matchToCamelCase.getValue(),
                                                                                   (name) -> protoObj.getDescriptorForType().findFieldByName(name) != null);
                Class<?> fromType = getFromType(protoObj, protoField, getProtoFieldGetterName(matchToCamelCase.getValue()));
                if (isCompatible(toType, fromType, protoObj, matchToDescriptor)) {
                    mapping.put(matchToCamelCase.getValue(), fieldName);
                } else {
                    throw new Proto3MapperException(String.format(INCOMPATIBLE_CLASS_BINDING_INCOMPATIBLE_FIELD_TYPE_MESSAGE, field.getName(), protoField.getName()));
                }
            } else {
                missingFields.add(fieldName);
            }
        }
        
        if (!missingFields.isEmpty()) {
            throw new Proto3MapperException(String.format(INCOMPATIBLE_CLASS_BINDING_MISSING_FIELDS_MESSAGE, missingFields));
        }
        
        return mapping;
    }
    
    private <P extends Message> boolean isCompatible(final Class<?> toType, final Class<?> fromType, final P protoObj, final StringCaseMapping caseMapping) {
        if (caseMapping == null) {
            return false;
        }
        Descriptors.FieldDescriptor descriptor = protoObj.getDescriptorForType().findFieldByName(caseMapping.getValue());
        return filterByFieldType(descriptor.getJavaType(),
                                 () -> {
                                     throw new UnsupportedOperationException("not implemented");
                                 },
                                 () -> true, //this will be validated by annotation in recursive field mapping call
                                 () -> fromType.equals(toType));
    }
    
    private <P extends Message> Class<?> getFromType(final P protoObj, final Field protoField, final String getterName) {
        final Class<?> fromType;
        try {
            System.out.println(getterName);
            Method getter = protoObj.getClass().getMethod(getterName);
            fromType = getter.getReturnType();
        } catch (NoSuchMethodException e) {
            throw new Proto3MapperException(String.format(COULD_NOT_RESOLVE_FIELD_MESSAGE, protoField));
        }
        return fromType;
    }
    
    private String withUnderscore(final String protoField) {
        return protoField + "_";
    }
    
    private String getProtoFieldGetterName(final String fieldName) {
        return PROTO_GETTER_PREFIX + CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, fieldName);
    }
    
}

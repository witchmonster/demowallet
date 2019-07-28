package com.betpawa.common.protomapper;

import java.lang.reflect.InvocationTargetException;

import com.google.protobuf.Message;

public class Proto3AnnotationMapper {
    
    static final String ANNOTATION_MISSING_FOR_CLASS_MESSAGE = "@ProroEntity annotation missing for class: %s";
    static final String ANNOTATION_DOES_NOT_MATCH_THE_DESTINATION_CLASS_MESSAGE = "Proto class %s in @Proto3Entity annotation does not match the destination class %s. Check annotated class: %s";
    static final String DEFAULT_CONSTRUCTOR_NOT_FOUND_FOR_CLASS_MESSAGE = "Default constructor not found for class: %s";
    static final String CONSTRUCTOR_NOT_PUBLIC_FOR_CLASS_MESSAGE = "Constructor not public for class: %s";
    static final String COULDNT_CREATE_AN_INSTANCE_OF_CLASS_MESSAGE = "Couldn't create an instance of class: %s";
    
    private Mapper createFieldMapperStrategy(final Proto3Strategy mappingStrategy) {
        switch (mappingStrategy) {
            case STRICT:
                throw new UnsupportedOperationException("not implemented");
            case DEFAULT:
            default:
                return new DefaultMapper(this);
        }
    }
    
    public <J, P extends Message> J map(Class<?> javaClass, P protoObj) {
        Proto3Entity binding = getBinding(javaClass, protoObj);
        
        J javaObj = newJavaInstance(javaClass);
        
        mapFields(javaClass, javaObj, protoObj, binding);
        
        return javaObj;
    }
    
    private <P extends Message> Proto3Entity getBinding(final Class<?> javaClass, final P protoObj) {
        Proto3Entity bindingAnnotation = findProtoEntityAnnotation(javaClass);
        
        if (bindingAnnotation == null) {
            throw new Proto3MapperException(String.format(ANNOTATION_MISSING_FOR_CLASS_MESSAGE, javaClass.getName()));
        }
        
        if (bindingAnnotation.value() != protoObj.getClass()) {
            throw new Proto3MapperException(String.format(ANNOTATION_DOES_NOT_MATCH_THE_DESTINATION_CLASS_MESSAGE,
                                                          bindingAnnotation.value().getName(),
                                                          protoObj.getClass().getName(),
                                                          javaClass.getName()));
        }
        
        return bindingAnnotation;
    }
    
    private <P extends Message> Proto3Entity findProtoEntityAnnotation(final Class<?> javaClass) {
        if (javaClass.isAnnotationPresent(Proto3Entity.class)) {
            return javaClass.getAnnotation(Proto3Entity.class);
        }
        return null;
    }
    
    private <J, P extends Message> void mapFields(final Class<?> javaClass, final J javaObj, final P protoObj, final Proto3Entity binding) {
        Proto3Strategy resolution = binding.strategy();
        
        Mapper mapper = createFieldMapperStrategy(resolution);
        
        mapper.mapFields(javaClass, protoObj, javaObj);
    }
    
    @SuppressWarnings("unchecked")
    private <J> J newJavaInstance(final Class<?> javaClass) {
        J javaObj;
        try {
            javaObj = (J) javaClass.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | NoSuchMethodException e) {
            throw new Proto3MapperException(String.format(DEFAULT_CONSTRUCTOR_NOT_FOUND_FOR_CLASS_MESSAGE, javaClass.getName()), e);
        } catch (IllegalAccessException e) {
            throw new Proto3MapperException(String.format(CONSTRUCTOR_NOT_PUBLIC_FOR_CLASS_MESSAGE, javaClass.getName()), e);
        } catch (InvocationTargetException e) {
            throw new Proto3MapperException(String.format(COULDNT_CREATE_AN_INSTANCE_OF_CLASS_MESSAGE, javaClass.getName()), e);
        }
        
        return javaObj;
    }
    
}
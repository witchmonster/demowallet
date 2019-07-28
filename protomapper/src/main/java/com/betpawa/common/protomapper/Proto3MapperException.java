package com.betpawa.common.protomapper;

class Proto3MapperException extends RuntimeException {
    Proto3MapperException(final String message, final Exception e) {
        super(message, e);
    }
    
    public Proto3MapperException(final String message) {
        super(message);
    }
}

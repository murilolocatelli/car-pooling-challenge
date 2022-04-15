package com.cabify.carpooling.exception;

public class RepeatedParameterException extends BaseException {

    private static final long serialVersionUID = -5951661245923818421L;

    public RepeatedParameterException(String... parameters) {
        super(parameters);
    }

}

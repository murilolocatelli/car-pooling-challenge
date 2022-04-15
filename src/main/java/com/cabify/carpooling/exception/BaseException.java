package com.cabify.carpooling.exception;

public class BaseException extends RuntimeException {

    private static final long serialVersionUID = 6441623083093324996L;

    private String[] parameters;

    public BaseException(final String... parameters) {
        this.parameters = parameters;
    }

    public String[] getParameters() {
        return this.parameters.clone();
    }

}

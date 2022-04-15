package com.cabify.carpooling.exception;

public class MalformedRequestException extends BaseException {

    private static final long serialVersionUID = 8003473998405526315L;

    public MalformedRequestException(String... parameters) {
        super(parameters);
    }

}

package com.cabify.carpooling.exception;

public class MissingParameterException extends BaseException {

    private static final long serialVersionUID = -598341517203314045L;

    public MissingParameterException(String... parameters) {
        super(parameters);
    }

}

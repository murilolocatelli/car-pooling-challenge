package com.cabify.carpooling.exception;

public class EntityAlreadyExistsException extends BaseException {

    private static final long serialVersionUID = 8258936993459103131L;

    public EntityAlreadyExistsException(final String... parameters) {
        super(parameters);
    }

}

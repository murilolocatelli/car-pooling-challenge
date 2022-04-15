package com.cabify.carpooling.controller;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public abstract class BaseController {

    protected ResponseEntity<Object> buildResponse(final HttpStatus status, final Object response) {

        Optional.ofNullable(status).orElseThrow(IllegalArgumentException::new);

        return new ResponseEntity<>(response, status);
    }

}

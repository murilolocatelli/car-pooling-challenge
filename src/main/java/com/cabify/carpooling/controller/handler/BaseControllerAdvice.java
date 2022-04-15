package com.cabify.carpooling.controller.handler;

import static java.text.MessageFormat.format;

import com.cabify.carpooling.dto.ResponseError;
import com.cabify.carpooling.exception.EntityAlreadyExistsException;
import com.cabify.carpooling.exception.EntityNotFoundException;
import com.cabify.carpooling.exception.MalformedRequestException;
import com.cabify.carpooling.exception.MissingParameterException;
import com.cabify.carpooling.exception.RepeatedParameterException;
import com.fasterxml.jackson.databind.JsonMappingException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class BaseControllerAdvice {

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public List<ResponseError> exception(final HttpMediaTypeNotSupportedException ex) {

        return Collections.singletonList(ResponseError.builder()
            .message(ex.getMessage())
            .build());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public List<ResponseError> exception(final MethodArgumentNotValidException ex) {

        final List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();

        final List<ResponseError> errorMessages = new ArrayList<>();

        fieldErrors.forEach(fieldError ->
            errorMessages.add(this.getResponseError(fieldError)));

        return errorMessages;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public List<ResponseError> exception(final HttpMessageNotReadableException ex) {

        final List<ResponseError> errorMessages = new ArrayList<>();

        final Throwable cause = ex.getCause();

        if (cause instanceof JsonMappingException) {

            final JsonMappingException jsonMappingException = (JsonMappingException) cause;

            final String field = this.getField(jsonMappingException);

            final String message = ex.getMessage();

            errorMessages.add(this.getResponseError(message, field));

        } else {

            errorMessages.add(this.getMalformedRequestResponseError());
        }

        return errorMessages;
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ResponseBody
    public List<ResponseError> exception(final HttpRequestMethodNotSupportedException ex) {

        return Collections.singletonList(ResponseError.builder()
            .message("Method not allowed")
            .build());
    }

    @ResponseBody
    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public List<ResponseError> exception(final EntityNotFoundException ex) {

        return Collections.singletonList(ResponseError.builder()
            .message(format("{0} not found", (Object[]) ex.getParameters()))
            .build());
    }

    @ResponseBody
    @ExceptionHandler(EntityAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public List<ResponseError> exception(final EntityAlreadyExistsException ex) {

        return Collections.singletonList(ResponseError.builder()
            .message(format("{0} already exists", (Object[]) ex.getParameters()))
            .build());
    }

    @ResponseBody
    @ExceptionHandler(MissingParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public List<ResponseError> exception(final MissingParameterException ex) {
        return Collections.singletonList(this.getMissingParameterResponseError(ex.getParameters()));
    }

    @ResponseBody
    @ExceptionHandler(RepeatedParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public List<ResponseError> exception(final RepeatedParameterException ex) {

        return Collections.singletonList(ResponseError.builder()
            .message(format(
                "Invalid parameter {0} - it must not be filled with a repeated value",
                (Object[]) ex.getParameters()))
            .build());
    }

    @ResponseBody
    @ExceptionHandler(MalformedRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public List<ResponseError> exception(final MalformedRequestException ex) {
        return Collections.singletonList(this.getMalformedRequestResponseError());
    }

    @ResponseBody
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public List<ResponseError> exception(final Exception ex) {

        final String developerMessage = "Internal server error {0}";

        return Collections.singletonList(ResponseError.builder()
            .message(format(developerMessage, ex.toString()))
            .build());
    }

    private ResponseError getResponseError(String message, final String fieldName) {

        message = Optional.ofNullable(message).orElse("");

        if (message.contains("java.lang.Integer")
            || message.contains("java.lang.Long")) {

            final String developerMessage =
                format("Invalid parameter {0} - it must be filled with a valid integer number", fieldName);

            return ResponseError.builder()
                .message(developerMessage)
                .build();

        } else {

            return this.getMalformedRequestResponseError();
        }
    }

    private ResponseError getResponseError(final FieldError fieldError) {

        final String fieldName = fieldError.getField();

        if ("NotNull".equalsIgnoreCase(fieldError.getCode())) {

            return this.getMissingParameterResponseError(new String[] {fieldName});

        } else {

            return this.getMalformedRequestResponseError();
        }
    }

    private ResponseError getMalformedRequestResponseError() {
        return ResponseError.builder()
            .message("Malformed request")
            .build();
    }

    private ResponseError getMissingParameterResponseError(final String[] parameters) {
        return ResponseError.builder()
            .message(format("Missing parameter {0}", (Object[]) parameters))
            .build();
    }

    private String getField(final JsonMappingException jsonMappingException) {
        return jsonMappingException.getPath().stream()
            .map(t -> t.getFieldName() != null ? t.getFieldName() : "[" + t.getIndex() + "]")
            .reduce((t, u) -> {
                if (u.contains("[")) {
                    return t + u;
                } else {
                    return t + "." + u;
                }
            })
            .orElse(null);
    }

}

package com.cabify.carpooling.service.impl;

import com.cabify.carpooling.service.JsonService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JsonServiceImpl implements JsonService {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public String toJsonString(final Object object) {

        return Optional.ofNullable(object)
            .map(t -> {
                try {
                    return objectMapper.writeValueAsString(object);
                } catch (JsonProcessingException e) {
                    return null;
                }
            })
            .orElse(null);
    }

    @Override
    public ObjectNode toObjectNode(final Object object) {

        return (ObjectNode) Optional.ofNullable(object)
            .map(t -> {
                try {
                    return objectMapper.readTree(toJsonString(t));
                } catch (IOException e) {
                    return null;
                }
            })
            .orElse(null);
    }

}

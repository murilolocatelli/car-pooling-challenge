package com.cabify.carpooling.service;

import com.fasterxml.jackson.databind.node.ObjectNode;

public interface JsonService {

    String toJsonString(Object object);

    ObjectNode toObjectNode(Object object);

}

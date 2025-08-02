package io.github.andreteixeir.literalura.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Implementation of IDataConverter using the Jackson library.
 * This class is responsible for deserializing JSON strings into Java objects.
 */
public class DataConverter implements IDataConverter {
    // ObjectMapper is the main class from Jackson that does the conversion.
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public <T> T getData(String json, Class<T> clazz) {
        try {
            // The readValue method attempts to parse the JSON and map it to the given class.
            return mapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            // If the JSON is malformed or doesn't match, an exception is thrown.
            System.err.println("Error parsing JSON: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
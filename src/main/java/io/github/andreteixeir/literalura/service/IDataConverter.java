package io.github.andreteixeir.literalura.service;

/**
 * An interface for data conversion services.
 * Defines a contract for converting data (e.g., a JSON string) into a specific object type.
 */
public interface IDataConverter {
    /**
     * Converts a data string into an object of the specified class.
     *
     * @param json  The JSON string to be converted.
     * @param clazz The class of the target object.
     * @param <T>   The generic type of the target object.
     * @return An object of type T populated with data from the source string.
     */
    <T> T getData(String json, Class<T> clazz);
}
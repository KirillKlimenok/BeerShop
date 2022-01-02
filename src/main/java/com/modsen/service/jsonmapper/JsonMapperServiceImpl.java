package com.modsen.service.jsonmapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonMapperServiceImpl implements JsonMapperService {
    private static final ObjectMapper objectMapper= new ObjectMapper();

    public <T> T getObj(String json, Class<T> tClass) throws JsonProcessingException {
        return objectMapper.readValue(json, tClass);
    }
}

package com.modsen.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.modsen.exception.BeerValidationException;
import com.modsen.exception.WrongDataException;
import lombok.Builder;

import java.util.function.Function;

@Builder
public class BeerCountValidatorService<T> implements Validator<T> {
    private Function<T, String> getCount;
    private ObjectMapper objectMapper;
    private String parameter;

    @Override
    public void check(T obj) {
        try {
            JsonNode jsonNode = objectMapper.readTree(getCount.apply(obj));
            int count = jsonNode.get(parameter).asInt();
            if (count < 0) {
                throw new BeerValidationException("you entered vary small count");
            }
        } catch (JsonProcessingException e) {
            throw new WrongDataException(e.getMessage());
        }
    }
}

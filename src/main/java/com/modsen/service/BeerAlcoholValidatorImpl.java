package com.modsen.service;

import com.modsen.exception.BeerValidationException;
import lombok.AllArgsConstructor;

import java.util.function.Function;

@AllArgsConstructor
public class BeerAlcoholValidatorImpl<T> implements Validator<T> {
    private Function<T, Float> functionGetAlcoholContent;
    private float minValue;
    private float maxValue;

    @Override
    public void check(T obj) {
        float objAlcoholContent = functionGetAlcoholContent.apply(obj);

        if (objAlcoholContent == 0) {
            throw new BeerValidationException("You don't entered alcohol content");
        }

        if (objAlcoholContent <= minValue) {
            throw new BeerValidationException("You entered vary small alcohol content");
        } else if (objAlcoholContent >= maxValue) {
            throw new BeerValidationException("You entered vary big alcohol content");
        }
    }
}

package com.modsen.service;

import com.modsen.exception.BeerValidationException;
import lombok.Builder;

import java.util.function.Function;

@Builder
public class BeerIbuValidatorService<T> implements Validator<T> {
    private Function<T, Integer> getIbu;
    private int max;
    private int min;

    @Override
    public void check(T obj) {
        int currentIbu = getIbu.apply(obj);

        if (currentIbu == 0) {
            throw new BeerValidationException("You don't entered ibu");
        }

        if (currentIbu < min) {
            throw new BeerValidationException("You entered vary small ibu");
        } else if (currentIbu > max) {
            throw new BeerValidationException("You entered vary big ibu");
        }
    }
}

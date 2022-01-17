package com.modsen.service;

import com.modsen.exception.BeerValidationException;
import lombok.AllArgsConstructor;

import java.util.function.Function;
import java.util.regex.Pattern;

@AllArgsConstructor
public class BeerTextFieldValidatorService<T> implements Validator<T> {
    private Function<T, String> getName;
    public final static String REGEX_FOR_BEER_NAME = "[a-zA-Zа-яЁёА-Я]{2,50}";
    private String parameter;

    @Override
    public void check(T obj) {
        String name = getName.apply(obj);

        if (name == null) {
            throw new BeerValidationException("You don't entered " + parameter);
        }

        if (!Pattern.matches(REGEX_FOR_BEER_NAME, name)) {
            throw new BeerValidationException("Wrong beer " + parameter);
        }
    }
}

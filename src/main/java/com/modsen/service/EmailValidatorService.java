package com.modsen.service;

import com.modsen.exception.ValidationException;

import java.util.function.Function;
import java.util.regex.Pattern;

public class EmailValidatorService<T> implements Validator<T> {
    private final Function<T, String> function;
    private static final String REGEX_FOR_EMAIL = "^([a-z0-9_-]+\\.)*[a-z0-9_-]+@[a-z0-9_-]+(\\.[a-z0-9_-]+)*\\.[a-z]{2,6}$";

    public EmailValidatorService(Function<T, String> function) {
        this.function = function;
    }

    @Override
    public void check(T user) {
        String email = function.apply(user);
        if (!Pattern.matches(REGEX_FOR_EMAIL, email)) {
            throw new ValidationException("Wrong email. please write email for example name@any.damain");
        }
    }
}

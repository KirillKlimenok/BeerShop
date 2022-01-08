package com.modsen.service;

import com.modsen.exception.ValidationException;

import java.util.function.Function;
import java.util.regex.Pattern;

public class LoginValidatorService<T> implements Validator<T> {
    private final Function<T, String> function;
    private static final String REGEX_FOR_LOGIN = "[a-zA-Z]{5,100}";

    public LoginValidatorService(Function<T, String> function) {
        this.function = function;
    }

    @Override
    public void check(T user) {
        String email = function.apply(user);
        if(!Pattern.matches(REGEX_FOR_LOGIN, email)){
            throw new ValidationException("Wrong login. please write login without numbers and use only latin letters");
        }
    }
}

package com.modsen.service;

import com.modsen.entitys.UserRequest;
import com.modsen.exceptions.ValidationException;

import java.util.regex.Pattern;

public class EmailValidatorService implements Validator {
    private final Validator validator;
    private static final String REGEX_FOR_EMAIL = "^([a-z0-9_-]+\\.)*[a-z0-9_-]+@[a-z0-9_-]+(\\.[a-z0-9_-]+)*\\.[a-z]{2,6}$";


    public EmailValidatorService() {
        this.validator = (Validator<UserRequest>) (obj) -> {
            if (!Pattern.matches(REGEX_FOR_EMAIL, obj.getEmail())) {
                throw new ValidationException("Wrong email. please write email for example name@any.damain");
            }
        };
    }

    @Override
    public void check(Object user) {
        validator.check(user);
    }
}

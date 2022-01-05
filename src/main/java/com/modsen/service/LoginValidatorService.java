package com.modsen.service;

import com.modsen.entitys.UserRequest;
import com.modsen.exceptions.ValidationException;

import java.util.regex.Pattern;

public class LoginValidatorService implements Validator {
    private final Validator validator;
    private static final String REGEX_FOR_LOGIN = "[a-zA-Z]{100}";

    public LoginValidatorService() {
        this.validator = (Validator<UserRequest>) (obj) -> {
            if (!Pattern.matches(REGEX_FOR_LOGIN, obj.getLogin())) {
                throw new ValidationException("Wrong login. please write login without numbers and use only latin letters");
            }
        };
    }

    @Override
    public void check(Object user) throws ValidationException {
        validator.check(user);
    }
}

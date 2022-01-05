package com.modsen.service;

import com.modsen.exceptions.NotTrueValidationUserException;

public class LoginValidatorService implements Validator {
    private final Validator validator;
    public LoginValidatorService(Validator validator) {
        this.validator = validator;
    }

    @Override
    public void check(Object user) throws NotTrueValidationUserException {
        validator.check(user);
    }
}

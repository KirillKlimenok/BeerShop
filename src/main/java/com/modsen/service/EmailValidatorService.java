package com.modsen.service;

public class EmailValidatorService implements Validator {
    private final Validator validator;
    private String massage;

    public EmailValidatorService(Validator validator) {
        this.validator = validator;
    }

    @Override
    public void check(Object user) {
        validator.check(user);
    }
}

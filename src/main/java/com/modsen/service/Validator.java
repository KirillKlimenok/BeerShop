package com.modsen.service;

import com.modsen.exceptions.NotTrueValidationUserException;

@FunctionalInterface
public interface Validator<T>{
    void check(T user);
}

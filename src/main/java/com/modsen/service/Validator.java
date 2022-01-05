package com.modsen.service;

public interface Validator {
    <T> boolean isValid(T user);
}

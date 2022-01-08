package com.modsen.service;

public interface Validator<T> {
    void check(T user);
}

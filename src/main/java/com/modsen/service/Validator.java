package com.modsen.service;

@FunctionalInterface
public interface Validator<T>{
    void check(T user);
}

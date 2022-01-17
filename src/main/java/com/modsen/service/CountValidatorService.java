package com.modsen.service;

import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.function.Consumer;
import java.util.function.Function;

@AllArgsConstructor
@Builder
public class CountValidatorService<T> implements Validator<T> {
    private Function<T, Integer> functionGetCount;
    private Consumer<T> setMinValue;
    private Consumer<T> setMaxValue;
    private int max;
    private int min;

    @Override
    public void check(T obj) {
        int value = functionGetCount.apply(obj);
        if (value < min) {
            setMinValue.accept(obj);
        } else if (value > max) {
            setMaxValue.accept(obj);
        }
    }
}

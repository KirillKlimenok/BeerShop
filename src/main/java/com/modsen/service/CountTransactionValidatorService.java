package com.modsen.service;

import com.modsen.exception.TooMuchValueCountTransactionException;
import com.modsen.exception.TooSmallValueCountTransactionException;

import java.util.Properties;
import java.util.function.Function;

public class CountTransactionValidatorService<T> implements Validator<T> {
    private int min;
    private int max;
    private Function function;

    public CountTransactionValidatorService(Function function, int max, int min) {
        this.function = function;
        this.min = min;
        this.max = max;
    }

    @Override
    public void check(T user) {
        int value = (Integer) function.apply(user);
        if(value < min){
            throw new TooSmallValueCountTransactionException();
        }else if(value>max){
            throw new TooMuchValueCountTransactionException();
        }
    }
}

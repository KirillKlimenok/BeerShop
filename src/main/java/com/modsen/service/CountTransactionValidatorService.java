package com.modsen.service;

import com.modsen.exception.TooMuchValueCountTransactionException;
import com.modsen.exception.TooSmallValueCountTransactionException;
import lombok.AllArgsConstructor;

import java.util.Properties;
import java.util.function.Function;

@AllArgsConstructor
public class CountTransactionValidatorService<T> implements Validator<T> {
    private Function function;
    private int max;
    private int min;

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

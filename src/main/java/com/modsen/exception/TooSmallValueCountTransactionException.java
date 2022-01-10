package com.modsen.exception;

public class TooSmallValueCountTransactionException extends ValidationException{
    public TooSmallValueCountTransactionException(String message) {
        super(message);
    }

    public TooSmallValueCountTransactionException() {
    }
}

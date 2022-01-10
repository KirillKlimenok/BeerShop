package com.modsen.exception;

public class TooMuchValueCountTransactionException extends ValidationException{
    public TooMuchValueCountTransactionException(String message) {
        super(message);
    }

    public TooMuchValueCountTransactionException() {
        super();
    }
}

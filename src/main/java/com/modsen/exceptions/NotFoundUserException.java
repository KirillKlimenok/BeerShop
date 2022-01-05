package com.modsen.exceptions;

public class NotFoundUserException extends MyCustomException{
    public NotFoundUserException(String message) {
        super(message);
    }

}

package com.modsen.exceptions;

import com.modsen.service.Validator;

public class NotTrueValidationUserException extends MyCustomException{
    public NotTrueValidationUserException(String message) {
        super(message);
    }
}

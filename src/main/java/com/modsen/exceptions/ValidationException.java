package com.modsen.exceptions;

import com.modsen.service.Validator;

public class ValidationException extends MyCustomException{
    public ValidationException(String message) {
        super(message);
    }
}

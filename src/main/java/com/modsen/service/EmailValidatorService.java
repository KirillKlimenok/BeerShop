package com.modsen.service;

import com.modsen.entitys.AbstractUser;

import java.util.regex.Pattern;

public class EmailValidatorService<T extends AbstractUser> implements Validator {
    @Override
    public <E> boolean isValid(E obj) {
        if(obj instanceof AbstractUser) {
            T users = (T) obj;
            String emailRegex = "^([a-z0-9_-]+\\.)*[a-z0-9_-]+@[a-z0-9_-]+(\\.[a-z0-9_-]+)*\\.[a-z]{2,6}$";
            return Pattern.matches(emailRegex, users.getEmail());
        }else{
            throw new ClassCastException("Email validation Exception with object" + obj);
        }
    }
}

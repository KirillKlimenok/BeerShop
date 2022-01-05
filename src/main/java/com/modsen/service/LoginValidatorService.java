package com.modsen.service;

import com.modsen.entitys.AbstractUser;

import java.util.regex.Pattern;

public class LoginValidatorService<T extends AbstractUser> implements Validator {
    @Override
    public <E> boolean isValid(E obj) {
        if(obj instanceof AbstractUser) {
            T user = (T) obj;
            return Pattern.matches("[a-zA-Z]{100}",user.getLogin());
        }else{
            throw new ClassCastException("Login validation Exception with object" + obj);
        }
    }
}

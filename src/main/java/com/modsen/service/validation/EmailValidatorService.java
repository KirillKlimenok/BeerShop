package com.modsen.service.validation;

import com.modsen.dto.UnregisteredUserDto;

import java.util.regex.Pattern;

public class EmailValidatorService implements Validator {
    @Override
    public boolean check(UnregisteredUserDto user) {
        String emailRegex = "^([a-z0-9_-]+\\.)*[a-z0-9_-]+@[a-z0-9_-]+(\\.[a-z0-9_-]+)*\\.[a-z]{2,6}$";
        return Pattern.matches(emailRegex,user.getEmail());
    }
}

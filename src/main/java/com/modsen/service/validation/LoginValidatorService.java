package com.modsen.service.validation;

import com.modsen.dto.UnregisteredUserDto;

import java.util.regex.Pattern;

public class LoginValidatorService implements Validator {
    @Override
    public boolean check(UnregisteredUserDto user) {
        return Pattern.matches("[a-zA-Z]*",user.getLogin());
    }
}

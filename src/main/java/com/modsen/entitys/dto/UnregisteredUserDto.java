package com.modsen.entitys.dto;

import com.modsen.entitys.AbstractUser;

public class UnregisteredUserDto extends AbstractUser {
    public UnregisteredUserDto(String login, String password, String email) {
        super(login, password, email);
    }
}

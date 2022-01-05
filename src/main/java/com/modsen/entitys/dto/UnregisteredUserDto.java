package com.modsen.entitys.dto;

import com.modsen.entitys.AbstractUser;
import com.modsen.entitys.UserRequest;

public class UnregisteredUserDto extends AbstractUser {
    public UnregisteredUserDto(String login, String password, String email) {
        super(login, password, email);
    }

    public UnregisteredUserDto(UserRequest userRequest) {
        super(userRequest.getLogin(), userRequest.getPassword(), userRequest.getEmail());
    }
}

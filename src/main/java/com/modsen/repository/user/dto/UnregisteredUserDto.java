package com.modsen.repository.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class UnregisteredUserDto {
    private String login;
    private String password;
    private String email;
    private UUID token;
}

package com.modsen.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UnregisteredUserDto {

    private String login;

    private String email;

    private String password;
}

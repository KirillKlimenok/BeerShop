package com.modsen.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class UnregisteredUserDto {
    private String login;
    private String password;
    private String email;
    private UUID token;
}

package com.modsen.entitys;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public abstract class AbstractUser {
    protected String login;
    protected String password;
    protected String email;
}

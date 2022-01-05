package com.modsen.entitys;

public class User extends AbstractUser{
    public User(String login, String password, String email) {
        super(login, password, email);
    }
}

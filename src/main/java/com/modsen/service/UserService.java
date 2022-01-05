package com.modsen.service;

import com.modsen.entitys.UserRequest;
import com.modsen.entitys.UserResponse;
import com.modsen.exceptions.ValidationException;
import com.modsen.entitys.dto.UnregisteredUserDto;

import java.sql.SQLException;
import java.util.List;

public interface UserService {
    void createNewUser(UserRequest unregisteredUserDto) throws SQLException;
    UserResponse getTokenUser(UserRequest unregisteredUserDto) throws SQLException;
}

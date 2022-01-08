package com.modsen.service;

import com.modsen.exception.UserNotFoundException;
import com.modsen.exception.UserRegistrationException;
import com.modsen.сontroller.model.UserRequest;
import com.modsen.сontroller.model.UserResponse;

import java.sql.SQLException;

public interface UserService {
    void createNewUser(UserRequest unregisteredUserDto) throws SQLException, UserRegistrationException;
    UserResponse getTokenUser(UserRequest unregisteredUserDto) throws SQLException, UserNotFoundException;
}

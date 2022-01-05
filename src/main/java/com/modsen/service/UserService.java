package com.modsen.service;

import com.modsen.exceptions.NotTrueValidationUserException;
import com.modsen.service.Validator;
import com.modsen.entitys.dto.UnregisteredUserDto;

import java.util.List;

public interface UserService {
    void createNewUser(UnregisteredUserDto unregisteredUserDto, List<Validator> validators) throws NotTrueValidationUserException;
    String getTokenUser(UnregisteredUserDto unregisteredUserDto);
}

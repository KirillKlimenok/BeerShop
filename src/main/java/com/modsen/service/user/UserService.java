package com.modsen.service.user;

import com.modsen.service.validation.Validator;
import com.modsen.dto.UnregisteredUserDto;

import java.util.List;

public interface UserService {
    void createNewUser(UnregisteredUserDto unregisteredUserDto, List<Validator> validators);
    String getTokenUser(UnregisteredUserDto unregisteredUserDto);
}

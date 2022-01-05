package com.modsen.service;

import com.modsen.entitys.AbstractUser;
import com.modsen.entitys.dto.UnregisteredUserDto;
import com.modsen.exceptions.ValidationException;

import java.util.List;

public interface ValidationsService {
    void validate(AbstractUser user, List<Validator> validators) throws ValidationException;
}

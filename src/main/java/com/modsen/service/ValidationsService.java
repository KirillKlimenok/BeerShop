package com.modsen.service;

import com.modsen.entitys.dto.UnregisteredUserDto;
import com.modsen.exceptions.NotTrueValidationUserException;

import java.util.List;

public interface ValidationsService {
    void validate(UnregisteredUserDto user, List<Validator> validators) throws NotTrueValidationUserException;
}

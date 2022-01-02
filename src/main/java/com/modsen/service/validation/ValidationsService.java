package com.modsen.service.validation;

import com.modsen.dto.UnregisteredUserDto;

import java.util.List;

public interface ValidationsService {
    boolean validate(UnregisteredUserDto user, List<Validator> validators);
}

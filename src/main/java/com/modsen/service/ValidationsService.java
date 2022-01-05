package com.modsen.service;

import com.modsen.entitys.dto.UnregisteredUserDto;

import java.util.List;

public interface ValidationsService {
    boolean validate(UnregisteredUserDto user, List<Validator> validators);
}

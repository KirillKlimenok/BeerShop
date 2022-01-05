package com.modsen.service;

import com.modsen.entitys.dto.UnregisteredUserDto;

import java.util.List;

public class ValidationsServiceImpl implements ValidationsService {

    @Override
    public boolean validate(UnregisteredUserDto user, List<Validator> validators) {
        return validators.stream().allMatch(x->x.isValid(user));
    }
}

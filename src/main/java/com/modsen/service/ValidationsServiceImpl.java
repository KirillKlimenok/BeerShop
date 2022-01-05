package com.modsen.service;

import com.modsen.entitys.AbstractUser;
import com.modsen.entitys.dto.UnregisteredUserDto;

import java.util.List;

public class ValidationsServiceImpl implements ValidationsService {
    @Override
    public void validate(AbstractUser user, List<Validator> validators) {
        validators.forEach(x -> x.check(user));
    }
}

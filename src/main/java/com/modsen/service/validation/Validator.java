package com.modsen.service.validation;

import com.modsen.dto.UnregisteredUserDto;

public interface Validator {
    boolean check(UnregisteredUserDto uses);
}

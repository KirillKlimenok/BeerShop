package modsen.com.service.validation;

import modsen.com.dto.UnregisteredUserDto;

public interface Validator {
    boolean check(UnregisteredUserDto uses);
}

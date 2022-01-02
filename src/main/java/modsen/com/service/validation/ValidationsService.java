package modsen.com.service.validation;

import modsen.com.dto.UnregisteredUserDto;

import java.util.List;

public interface ValidationsService {
    boolean validate(UnregisteredUserDto user, List<Validator> validators);
}

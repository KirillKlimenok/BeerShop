package modsen.com.service.validation;

import modsen.com.dto.UnregisteredUserDto;

import java.util.ArrayList;

public interface ValidationsService {
    boolean validate(UnregisteredUserDto user, ArrayList<Validator> validators);
}

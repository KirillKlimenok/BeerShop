package modsen.com.service.validation;

import modsen.com.dto.UnregisteredUserDto;

import java.util.List;

public class ValidationsServiceImpl implements ValidationsService {

    @Override
    public boolean validate(UnregisteredUserDto user, List<Validator> validators) {
        for (Validator validator:validators){
            if(!validator.check(user)){
                return false;
            }
        }
        return true;
    }
}

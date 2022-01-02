package modsen.com.service.user;

import modsen.com.dto.UnregisteredUserDto;
import modsen.com.service.validation.Validator;

import java.util.List;

public interface UserService {
    void createNewUser(UnregisteredUserDto unregisteredUserDto, List<Validator> validators);
    String getTokenUser(UnregisteredUserDto unregisteredUserDto);
}

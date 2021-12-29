package modsen.com.service.user;

import modsen.com.dto.UnregisteredUserDto;

public interface UserService {
    void createNewUser(UnregisteredUserDto unregisteredUserDto);
    String getTokenUser(UnregisteredUserDto unregisteredUserDto);
}

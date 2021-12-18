package modsen.com.repository.UserRepository;

import modsen.com.service.UnregisteredUserSevice.UnregisteredUserService;

public class UserRepository implements ReadUserTokenRepository, WriteNewUserRepository{


    @Override
    public String read() {
        return null;
    }

    @Override
    public boolean write(UnregisteredUserService userService) {
        return false;
    }
}

package modsen.com.repository.UserRepository;

import modsen.com.service.UnregisteredUserSevice.UnregisteredUserService;

public interface WriteNewUserRepository {
    boolean write(UnregisteredUserService userService);
}

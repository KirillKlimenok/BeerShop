package modsen.com.repository.UserRepository;

import modsen.com.service.UnregisteredUserSevice.UnregisteredUserService;

import java.sql.SQLException;

public interface WriteNewUserRepository {
    boolean writeUser(UnregisteredUserService userService) throws SQLException;
}

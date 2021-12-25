package modsen.com.repository.UserRepository;

import modsen.com.dto.UnregisteredUserDto;

import java.sql.SQLException;

public interface WriteNewUserRepository {
    boolean writeUser(UnregisteredUserDto userService) throws SQLException;
}

package modsen.com.repository.UserRepository;

import modsen.com.service.UnregisteredUserSevice.UnregisteredUserService;

import java.sql.SQLException;

public interface ReadUserTokenRepository {
    String getUserToken(UnregisteredUserService registeredUserService) throws SQLException;
}

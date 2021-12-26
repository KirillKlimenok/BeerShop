package modsen.com.repository.user;

import modsen.com.dto.UnregisteredUserDto;

import java.sql.SQLException;

public interface ReadUserTokenRepository {
    String getUserToken(UnregisteredUserDto registeredUserService) throws SQLException;
}

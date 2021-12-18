package modsen.com.repository.UserRepository;

import modsen.com.repository.ConnactionsRepository.ConnectionRepository;
import modsen.com.service.UnregisteredUserSevice.UnregisteredUserService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public class UserRepository implements ReadUserTokenRepository, WriteNewUserRepository {
    ConnectionRepository connectionRepository = new ConnectionRepository();

    public String getUserToken(UnregisteredUserService user) throws SQLException {
        try {
            Connection connection = connectionRepository.getConnection();
            String login = user.getLogin();
            String password = user.getPassword();

            PreparedStatement preparedStatement = connection.prepareStatement(
                    "select token from users_token where id = (select id from users_list where login = ? and password = ?)");
            preparedStatement.setString(1, login);
            preparedStatement.setString(2, password);

            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            return resultSet.getString("token");
        } catch (SQLException e) {
            return null;
        }
    }

    @Override
    public boolean write(UnregisteredUserService userService) {
        userService.setPassword(Objects.hash(userService.getPassword())+"");
    return false;
    }
}

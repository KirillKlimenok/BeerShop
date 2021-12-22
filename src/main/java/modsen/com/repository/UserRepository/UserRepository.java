package modsen.com.repository.UserRepository;

import modsen.com.repository.ConnactionsRepository.ConnectionRepository;
import modsen.com.service.TokenService.TokenService;
import modsen.com.service.UnregisteredUserSevice.UnregisteredUserService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class UserRepository implements ReadUserTokenRepository, WriteNewUserRepository {
    ConnectionRepository connectionRepository = new ConnectionRepository();

    @Override
    public String getUserToken(UnregisteredUserService user) throws SQLException {
        Connection connection = connectionRepository.getConnection();
        String userId = getUserId(connection, user);

        PreparedStatement preparedStatement = connection.prepareStatement(
                "select token from users_token where id_user = ?");
        preparedStatement.setObject(1, UUID.fromString(userId));

        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.next();
        return resultSet.getString("token");

    }

    @Override
    public boolean writeUser(UnregisteredUserService user) throws SQLException {
        if (getUserToken(user) == null) {
            PreparedStatement preparedStatement;

            String token = new TokenService().getToken(user);
            Connection connection = connectionRepository.getConnection();

            preparedStatement = connection.prepareStatement(
                    "insert into users_list(login, password) VALUES (?,?)");
            preparedStatement.setString(1, user.getLogin());
            preparedStatement.setString(2, user.getPassword());

            if (!preparedStatement.execute()) {
                writeToken(token, connection, user);
            }
        }
        return false;
    }

    private boolean writeToken(String token, Connection connection, UnregisteredUserService user) throws SQLException {
        String userId = getUserId(connection, user);
        PreparedStatement preparedStatement;

        preparedStatement = connection.prepareStatement(
                "insert into users_token(token, id_user) VALUES (?,?)");
        preparedStatement.setString(1, token);
        preparedStatement.setObject(2, UUID.fromString(userId));
        return preparedStatement.execute();
    }

    private String getUserId(Connection connection, UnregisteredUserService user) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(
                "select * from users_list where login  = ? and password = ?");
        preparedStatement.setString(1, user.getLogin());
        preparedStatement.setString(2, user.getPassword());

        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.next();
        return resultSet.getString("id");
    }

}

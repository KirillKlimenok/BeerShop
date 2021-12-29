package modsen.com.repository.user;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j;
import modsen.com.exceptions.NotFoundUserException;
import modsen.com.repository.connections.ConnectionRepository;
import modsen.com.repository.connections.ConnectionRepositoryImpl;
import modsen.com.service.token.TokenServiceImpl;
import modsen.com.dto.UnregisteredUserDto;
import org.apache.log4j.Priority;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@Log4j
public class UserRepository {
    private ConnectionRepository connectionRepository;
    private String sqlScriptForGetUserToken = "select token from users_token where id_user = ?";
    private String sqlScriptForAddUserInDb = "insert into users_list(login, email, password) VALUES (?,?,?)";
    private String sqlScriptForFindUserInDb = "select * from users_list where email = ?";
    private String sqlScriptForWriteUserToken = "insert into users_token(token, id_user) VALUES (?,?)";
    private String sqlScriptForFindUserForAuth = "select id from users_list where login  = ? and password = ?";

    public UserRepository() {
        connectionRepository = new ConnectionRepositoryImpl();
    }

    public String getUserToken(UnregisteredUserDto user) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = connectionRepository.connect();
            String userId = getUserId(connection, user);

            preparedStatement = connection.prepareStatement(sqlScriptForGetUserToken);
            preparedStatement.setObject(1, UUID.fromString(userId));

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getString("token");
            } else {
                throw new NotFoundUserException(user + " not found");
            }
        } catch (SQLException e) {
            throw new NotFoundUserException(user + " not found");
        } finally {
            try {
                connection.close();
                preparedStatement.close();
            } catch (SQLException ignored) {
            }
        }
    }

    public boolean writeUser(UnregisteredUserDto user) throws SQLException {
        if (!isRegisteredUser(user)) {
            PreparedStatement preparedStatement;

            String token = new TokenServiceImpl().getToken(user);
            Connection connection = connectionRepository.connect();

            preparedStatement = connection.prepareStatement(sqlScriptForAddUserInDb);
            preparedStatement.setString(1, user.getLogin());
            preparedStatement.setString(2, user.getEmail());
            preparedStatement.setString(3, user.getPassword());

            if (!preparedStatement.execute()) {
                return writeToken(token, connection, user);
            }
        }
        return false;
    }

    public boolean isRegisteredUser(UnregisteredUserDto user) {
        try {
            Connection connection = connectionRepository.connect();
            PreparedStatement preparedStatement;

            preparedStatement = connection.prepareStatement(sqlScriptForFindUserInDb);
            preparedStatement.setString(1, user.getEmail());

            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            return !resultSet.getString("id").isEmpty();
        } catch (SQLException e) {
            return false;
        }
    }

    private boolean writeToken(String token, Connection connection, UnregisteredUserDto user) throws SQLException {
        String userId = getUserId(connection, user);
        PreparedStatement preparedStatement;

        preparedStatement = connection.prepareStatement(sqlScriptForWriteUserToken);
        preparedStatement.setString(1, token);
        preparedStatement.setObject(2, UUID.fromString(userId));
        return preparedStatement.execute();
    }

    private String getUserId(Connection connection, UnregisteredUserDto user) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(sqlScriptForFindUserForAuth);
        preparedStatement.setString(1, user.getLogin());
        preparedStatement.setString(2, user.getPassword());

        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            return resultSet.getString("id");
        } else {
            throw new NotFoundUserException(user + "not found");
        }
    }

}

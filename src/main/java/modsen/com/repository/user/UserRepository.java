package modsen.com.repository.user;

import lombok.extern.log4j.Log4j;
import modsen.com.dto.UnregisteredUserDto;
import modsen.com.exceptions.NotFoundUserException;
import modsen.com.repository.connections.ConnectionRepository;
import modsen.com.repository.connections.ConnectionRepositoryImpl;
import org.apache.log4j.Priority;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@Log4j
public class UserRepository {
    private final ConnectionRepository connectionRepository;
    private final String sqlScriptForGetUserToken = "select token from users_token where id_user = ?";
    private final String sqlScriptForAddUserInDb = "insert into users_list(login, email, password) VALUES (?,?,?)";
    private final String sqlScriptForFindUserInDb = "select * from users_list where email = ?";
    private final String sqlScriptForWriteUserToken = "insert into users_token(token) VALUES (?)";
    private final String sqlScriptForFindUserForAuth = "select id from users_list where login  = ? and password = ?";

    public UserRepository() {
        connectionRepository = new ConnectionRepositoryImpl();
    }

    public String getUserToken(UnregisteredUserDto user) {
        try (Connection connection = connectionRepository.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlScriptForGetUserToken)) {
            String userId = getUserId(user);

            preparedStatement.setObject(1, UUID.fromString(userId));

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getString("token");
            } else {
                throw new NotFoundUserException(user + " not found");
            }
        } catch (SQLException e) {
            log.log(Priority.WARN, e.getMessage());
            throw new NotFoundUserException(user + " not found");
        }
    }

    public boolean writeUser(UnregisteredUserDto user) throws SQLException {
        try (Connection connection = connectionRepository.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlScriptForAddUserInDb)) {
            preparedStatement.setString(1, user.getLogin());
            preparedStatement.setString(2, user.getEmail());
            preparedStatement.setString(3, user.getPassword());

            return preparedStatement.execute();
        }
    }

    public boolean isRegisteredUser(UnregisteredUserDto user) {
        try (Connection connection = connectionRepository.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlScriptForFindUserInDb)) {
            preparedStatement.setString(1, user.getEmail());

            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            return !resultSet.getString("id").isEmpty();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public boolean writeToken(String token) throws SQLException {
        try (Connection connection = connectionRepository.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlScriptForWriteUserToken)) {

            preparedStatement.setObject(1,  UUID.fromString(token));
            return preparedStatement.execute();
        }
    }

    public String getUserId(UnregisteredUserDto user) throws SQLException {
        Connection connection = connectionRepository.connect();
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

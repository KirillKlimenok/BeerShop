package com.modsen.repository.user;

import lombok.extern.log4j.Log4j;
import com.modsen.dto.UnregisteredUserDto;
import com.modsen.exceptions.NotFoundUserException;
import com.modsen.repository.connections.ConnectionRepository;
import com.modsen.repository.connections.ConnectionRepositoryImpl;
import org.apache.log4j.Priority;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@Log4j
public class UserRepository {
    private static final ConnectionRepository connectionRepository = new ConnectionRepositoryImpl();
    private final String sqlScriptForGetUserToken = "select token from users_token where token = ?";
    private final String sqlScriptForAddUserInDb = "insert into users_list(login, email, password) VALUES (?,?,?)";
    private final String sqlScriptForFindUserInDb = "select * from users_list where email = ? or login = ?";
    private final String sqlScriptForWriteUserToken = "insert into users_token(token) VALUES (?)";
    private final String sqlScriptForFindUserForAuth = "select id from users_list where login  = ? and password = ?";

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
            log.error(e.getMessage());
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

    public boolean isRegisteredUser(UnregisteredUserDto user) throws SQLException {
        try (Connection connection = connectionRepository.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlScriptForFindUserInDb)) {
            preparedStatement.setString(1, user.getEmail());
            preparedStatement.setString(2, user.getLogin());

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return !resultSet.getString("id").isEmpty();
            } else {
                return false;
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw new SQLException(e.getMessage());
        }
    }

    public boolean writeToken(String token) throws SQLException {
        try (Connection connection = connectionRepository.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlScriptForWriteUserToken)) {

            preparedStatement.setObject(1, UUID.fromString(token));
            return preparedStatement.execute();
        }
    }

    public String getUserId(UnregisteredUserDto user) throws SQLException {
        try (Connection connection = connectionRepository.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlScriptForFindUserForAuth)) {
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

}

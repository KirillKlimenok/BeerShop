package com.modsen.repository.user;

import lombok.extern.log4j.Log4j;
import com.modsen.entitys.dto.UnregisteredUserDto;
import com.modsen.exceptions.NotFoundUserException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

@Log4j
public class UserRepository {
    private static final String SQL_SCRIPT_FOR_GET_USER_TOKEN = "select token from users_token where token = ?";
    private static final String SQL_SCRIPT_FOR_ADD_USER_IN_DB = "insert into users_list(login, email, password) VALUES (?,?,?)";
    private static final String SQL_SCRIPT_FOR_FIND_USER_IN_DB = "select * from users_list where email = ? or login = ?";
    private static final String SQL_SCRIPT_FOR_WRITE_USER_TOKEN = "insert into users_token(token) VALUES (?)";
    private static final String SQL_SCRIPT_FOR_FIND_USER_FOR_AUTH = "select id from users_list where login  = ? and password = ?";

    private DataSource dataSource;

    public UserRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Optional<String> getUserToken(UnregisteredUserDto user) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_SCRIPT_FOR_GET_USER_TOKEN)) {
            String userId = getUserId(user).get();
            preparedStatement.setObject(1, UUID.fromString(userId));

            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            return Optional.ofNullable(resultSet.getString("token"));
        }
    }

    public boolean writeUser(UnregisteredUserDto user) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_SCRIPT_FOR_ADD_USER_IN_DB)) {
            preparedStatement.setString(1, user.getLogin());
            preparedStatement.setString(2, user.getEmail());
            preparedStatement.setString(3, user.getPassword());

            return preparedStatement.execute();
        }
    }

    public Optional<String> checkUser(UnregisteredUserDto user) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_SCRIPT_FOR_FIND_USER_IN_DB)) {
            preparedStatement.setString(1, user.getEmail());
            preparedStatement.setString(2, user.getLogin());

            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            return Optional.ofNullable(resultSet.getString("id"));
        }
    }

    public boolean writeToken(String token) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_SCRIPT_FOR_WRITE_USER_TOKEN)) {

            preparedStatement.setObject(1, UUID.fromString(token));
            return preparedStatement.execute();
        }
    }

    public Optional<String> getUserId(UnregisteredUserDto user) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_SCRIPT_FOR_FIND_USER_FOR_AUTH)) {
            preparedStatement.setString(1, user.getLogin());
            preparedStatement.setString(2, user.getPassword());

            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            return Optional.ofNullable(resultSet.getString("id"));
        }
    }
}



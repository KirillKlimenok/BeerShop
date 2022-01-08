package com.modsen.repository;

import com.modsen.service.dto.UnregisteredUserDto;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

@Log4j
@AllArgsConstructor
public class UserRepository {
    private static final String SQL_SCRIPT_FOR_GET_USER_TOKEN = "select token from users_token where token = (select id from users_list where login = ? and password = ?);";
    private static final String SQL_SCRIPT_FOR_ADD_USER_IN_DB = "insert into users_list(id, login, email, password) VALUES (?,?,?,?); insert into users_token(token) VALUES (?)";
    private static final String SQL_SCRIPT_FOR_FIND_USER_IN_DB = "select * from users_list where ? = ?";

    private DataSource dataSource;

    public Optional<String> getUserToken(String userLogin, String userPassword) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_SCRIPT_FOR_GET_USER_TOKEN)) {
            preparedStatement.setString(1, userLogin);
            preparedStatement.setString(2, userPassword);

            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            return Optional.ofNullable(resultSet.getString("token"));
        }
    }

    public void save(UnregisteredUserDto user) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_SCRIPT_FOR_ADD_USER_IN_DB)) {
            preparedStatement.setObject(1, user.getToken());
            preparedStatement.setString(2, user.getLogin());
            preparedStatement.setString(3, user.getEmail());
            preparedStatement.setString(4, user.getPassword());
            preparedStatement.setObject(5, user.getToken());

            preparedStatement.execute();
            connection.commit();
        }
    }

    public boolean isUserExist(String argument, String value) throws SQLException {
        String sql = SQL_SCRIPT_FOR_FIND_USER_IN_DB.replaceFirst("[?]", argument);
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, value);

            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        }
    }
}



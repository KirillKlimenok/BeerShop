package com.modsen.repository;

import lombok.AllArgsConstructor;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@AllArgsConstructor
public class AdminRepository {
    private static final String SQL_SCRIPT_FOR_CHECK_IS_USER_ADMIN = "select id from admins where user_id = ?";
    private DataSource dataSource;


    public boolean isUserAdmin(UUID token) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_SCRIPT_FOR_CHECK_IS_USER_ADMIN)) {
            preparedStatement.setObject(1, token);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        }
    }
}

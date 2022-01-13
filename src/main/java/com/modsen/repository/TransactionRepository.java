package com.modsen.repository;

import com.modsen.repository.entytie.UserTransactions;
import lombok.AllArgsConstructor;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
public class TransactionRepository {
    public static final String SQL_SCRIPT_FOR_GET_TRANSACTION_BY_USER_WITH_LIMIT = "select id_beer, count, date_time from users_transactions where id_user = ? limit ?;";

    private DataSource dataSource;

    public List<UserTransactions> getTransaction(String userToken, int countTransaction) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_SCRIPT_FOR_GET_TRANSACTION_BY_USER_WITH_LIMIT)) {
            preparedStatement.setObject(1, UUID.fromString(userToken));
            preparedStatement.setInt(2, countTransaction);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<UserTransactions> userTransactions = new ArrayList<>();

            while (resultSet.next()) {
                userTransactions.add(new UserTransactions(resultSet.getInt("id_beer"), resultSet.getInt("count"), resultSet.getTimestamp("date_time").toLocalDateTime()));
            }
            return userTransactions;
        }
    }
}

package com.modsen.repository;

import com.modsen.repository.entytie.UserTransactions;
import com.modsen.service.dto.TransactionDto;
import lombok.AllArgsConstructor;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
public class TransactionRepository {
    public static final String SQL_SCRIPT_FOR_GET_TRANSACTION_BY_USER_WITH_LIMIT = "select id_beer, count, date_time from users_transactions where id_user = ? limit ?;";
    public static final String SQL_SCRIPT_FOR_SAVE_TRANSACTION = "insert into users_transactions(id_user, id_beer, count, date_time) values (?,?,?,?)";
    public static final String TRANSACTION_SQL = ",(?,?,?,?)";

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

    public void save(List<TransactionDto> transactionsDto, String userToken) throws SQLException {
        String script = SQL_SCRIPT_FOR_SAVE_TRANSACTION;
        script += TRANSACTION_SQL.repeat(transactionsDto.size() - 1);

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(script)) {
            int j = 1;
            for (int i = 1; i <= transactionsDto.size(); i++) {
                TransactionDto transactionDto = transactionsDto.get(i - 1);
                preparedStatement.setObject(j, UUID.fromString(userToken));
                preparedStatement.setInt(j + 1, transactionDto.getIdBeer());
                preparedStatement.setDouble(j + 2, transactionDto.getCountBeer());
                preparedStatement.setTimestamp(j + 3, Timestamp.valueOf(transactionDto.getBuyDate()));
                j = j + 4;
            }
            preparedStatement.execute();
            connection.commit();
        }
    }
}

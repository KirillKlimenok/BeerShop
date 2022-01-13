package com.modsen.repository;

import com.modsen.service.dto.TransactionDto;
import com.modsen.сontroller.model.BeerRequest;
import lombok.AllArgsConstructor;
import org.postgresql.util.PGobject;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
public class UserActionRepository {
    private static final String SQL_SCRIPT_FOR_CHANGE_BEER_COUNT_BEER = "update beers set count_containers = ? where id = ?;";
    public static final String SQL_SCRIPT_FOR_SAVE_TRANSACTION = "insert into users_transactions(id_user, id_beer, count, date_time) values (?,?,?,?)";
    public static final String TRANSACTION_SQL = ",(?,?,?,?)";

    private final DataSource dataSource;

    public void updateCountBeerAndSaveTransactions(List<BeerRequest> beerRequests, List<TransactionDto> transactionDtoList, String userToken) throws SQLException {
        String beersSql = SQL_SCRIPT_FOR_CHANGE_BEER_COUNT_BEER;
        beersSql += SQL_SCRIPT_FOR_CHANGE_BEER_COUNT_BEER.repeat(beerRequests.size() - 1);

        String transactionsSql = SQL_SCRIPT_FOR_SAVE_TRANSACTION;
        transactionsSql += TRANSACTION_SQL.repeat(transactionDtoList.size() - 1);

        String sqlScript = beersSql + transactionsSql;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlScript)) {
            int j = 1;
            PGobject jsonObject = new PGobject();
            jsonObject.setType("json");
            for (int i = 1; i <= beerRequests.size(); i++) {
                jsonObject.setValue(beerRequests.get(i - 1).getCountJson());
                preparedStatement.setObject(j, jsonObject);
                preparedStatement.setInt(j + 1, beerRequests.get(i - 1).getIdBeer());
                j = j + 2;
            }

            for (int i = 1; i <= transactionDtoList.size(); i++) {
                TransactionDto transactionDto = transactionDtoList.get(i - 1);
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


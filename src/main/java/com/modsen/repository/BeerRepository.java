package com.modsen.repository;

import com.modsen.service.dto.BeerDto;
import lombok.AllArgsConstructor;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
public class BeerRepository {
    private static final String SQL_SCRIPT_FOR_CREATE_NEW_BEER = "insert into beers (name, id_container, id_beer_type, alcohol_content, ibu, count_containers) values (?,?,?,?,?,?);";
    private static final String SQL_SCRIPT_FOR_ADD_BEER_COUNT_BEER = "update beers set count_containers = beers.count_containers + ? where id = ?;";
    private static final String SQL_SCRIPT_FOR_REMOVE_BEER_COUNT_BEER = "update beers set count_containers = beers.count_containers - ? where id = ?;";
    private static final String SQL_SCRIPT_FOR_FIND_BEER_ID = "select id from beers where name = ? and id_container = ? and id_beer_type = ?;";
    private static final String SQL_SCRIPT_FOR_CHECK_BEER = "select id from beers where id = ?;";
    private static final String SQL_SCRIPT_FOR_GET_ALL_BEERS = "select * from beers";

    private final DataSource dataSource;


    public void addNewBeer(BeerDto beerDto) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_SCRIPT_FOR_CREATE_NEW_BEER)) {
            preparedStatement.setString(1, beerDto.getName());
            preparedStatement.setInt(2, beerDto.getIdContainer());
            preparedStatement.setInt(3, beerDto.getIdBeerType());
            preparedStatement.setFloat(4, beerDto.getAlcoholContent());
            preparedStatement.setInt(5, beerDto.getIbu());
            preparedStatement.setInt(6, beerDto.getCountBeer());

            preparedStatement.execute();
            connection.commit();
        }
    }

    public Optional<Integer> getBeerId(BeerDto beerDto) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_SCRIPT_FOR_CREATE_NEW_BEER)) {
            preparedStatement.setString(1, beerDto.getName());
            preparedStatement.setInt(2, beerDto.getIdContainer());
            preparedStatement.setInt(3, beerDto.getIdBeerType());

            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();

            return Optional.of(resultSet.getInt("id"));
        }
    }

    public void addCountBeer(int idBeer, int count) throws SQLException {
        updateCountBeer(idBeer, count, SQL_SCRIPT_FOR_ADD_BEER_COUNT_BEER);
    }

    public void removeCountBeer(int idBeer, int count) throws SQLException {
        updateCountBeer(idBeer, count, SQL_SCRIPT_FOR_REMOVE_BEER_COUNT_BEER);
    }

    private void updateCountBeer(int idBeer, int count, String sqlScript) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlScript)) {
            preparedStatement.setInt(1, count);
            preparedStatement.setInt(2, idBeer);

            preparedStatement.execute();
            connection.commit();
        }
    }

    public List<Integer> getAllBeers() throws SQLException {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(SQL_SCRIPT_FOR_GET_ALL_BEERS);
            List<Integer> beersId = new ArrayList<>();
            while (resultSet.next()) {
                beersId.add(resultSet.getInt("id"));
            }
            return beersId;
        }
    }

}

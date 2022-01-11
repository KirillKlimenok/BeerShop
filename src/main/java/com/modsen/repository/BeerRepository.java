package com.modsen.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.modsen.repository.entytie.Beer;
import com.modsen.repository.entytie.BeerContainer;
import com.modsen.repository.entytie.BeerType;
import com.modsen.service.dto.BeerDto;
import com.modsen.сontroller.model.BeerRequest;
import lombok.AllArgsConstructor;
import org.postgresql.util.PGobject;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class BeerRepository {
    private static final String SQL_SCRIPT_FOR_CREATE_NEW_BEER = "insert into beers (name, id_container, id_beer_type, alcohol_content, ibu, count_containers) values (?,?,?,?,?,?);";
    private static final String SQL_SCRIPT_FOR_CHANGE_BEER_COUNT_BEER = "update beers set count_containers = ? where id = ?;";
    private static final String SQL_SCRIPT_FOR_GET_ALL_BEERS_WITH_LIMIT = "select * from beers limit ?";
    private static final String SQL_SCRIPT_FOR_GET_ALL_BEER_CONTAINERS = "select * from  beer_containers";
    private static final String SQL_SCRIPT_FOR_GET_ALL_BEER_TYPES = "select * from  beer_types";
    private static final String SQL_SCRIPT_FOR_GET_BEER_BY_ID_WITH_COUNT = "select id, name, id_container, id_beer_type, alcohol_content, ibu, count_containers from  beers where id = ?";
    private static final String SQL_FOR_ADD_NEW_BEER_ID = "or id =?";

    private final DataSource dataSource;

    public void save(BeerDto beerDto) throws SQLException {
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

    public List<Beer> getBeerByIds(List<Integer> beersIds) throws SQLException {
        String sqlScript = SQL_SCRIPT_FOR_GET_BEER_BY_ID_WITH_COUNT;
        sqlScript += SQL_FOR_ADD_NEW_BEER_ID.repeat(beersIds.size() - 1);

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlScript)) {
            for (int i = 1; i <= beersIds.size(); i++) {
                preparedStatement.setInt(i, beersIds.get(i - 1));
            }

            return getBeersList(preparedStatement);
        }
    }

    public void updateCountBeer(List<BeerRequest> beerRequests) throws SQLException {
        String scriptWithAllBeerWhoChange = SQL_SCRIPT_FOR_CHANGE_BEER_COUNT_BEER;
        scriptWithAllBeerWhoChange += SQL_SCRIPT_FOR_CHANGE_BEER_COUNT_BEER.repeat(beerRequests.size() - 1);

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(scriptWithAllBeerWhoChange)) {
            int j = 1;
            PGobject jsonObject = new PGobject();
            jsonObject.setType("json");
            for (int i = 1; i <= beerRequests.size(); i++) {
                jsonObject.setValue(beerRequests.get(i - 1).getCountJson());
                preparedStatement.setObject(j, jsonObject);
                preparedStatement.setInt(j + 1, beerRequests.get(i - 1).getIdBeer());
                j = j + 2;
            }
            preparedStatement.execute();
            connection.commit();
        }
    }

    public List<Beer> getBeerList(int countBeer) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_SCRIPT_FOR_GET_ALL_BEERS_WITH_LIMIT)) {
            statement.setInt(1, countBeer);
            return getBeersList(statement);
        }
    }

    private List<Beer> getBeersList(PreparedStatement statement) throws SQLException {
        ResultSet resultSet = statement.executeQuery();

        List<Beer> beersList = new ArrayList<>();
        while (resultSet.next()) {
            Beer beer = Beer.builder().
                    id(resultSet.getInt("id")).
                    name(resultSet.getString("name")).
                    idContainer(resultSet.getInt("id_container")).
                    idTypeBeer(resultSet.getInt("id_beer_type")).
                    alcoholContent(resultSet.getFloat("alcohol_content")).
                    ibu(resultSet.getInt("ibu")).
                    countBeerJson(resultSet.getString("count_containers")).
                    build();
            beersList.add(beer);
        }

        return beersList;
    }

    public Map<Integer, BeerContainer> getBeerContainers() throws SQLException {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(SQL_SCRIPT_FOR_GET_ALL_BEER_CONTAINERS);

            Map<Integer, BeerContainer> beerContainers = new HashMap<>();
            while (resultSet.next()) {
                BeerContainer beerContainer = BeerContainer.builder().
                        name(resultSet.getString("name_type")).
                        volume(resultSet.getFloat("volume")).
                        build();
                beerContainers.put(resultSet.getInt("id"), beerContainer);
            }

            return beerContainers;
        }
    }

    public Map<Integer, BeerType> getBeerTypes() throws SQLException {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(SQL_SCRIPT_FOR_GET_ALL_BEER_TYPES);

            Map<Integer, BeerType> beerTypes = new HashMap<>();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("type_name");
                beerTypes.put(id, new BeerType(name));
            }

            return beerTypes;
        }
    }
}

package com.modsen.repository;

import com.modsen.repository.entytie.Beer;
import com.modsen.repository.entytie.BeerContainer;
import com.modsen.repository.entytie.BeerType;
import com.modsen.service.dto.BeerDto;
import lombok.AllArgsConstructor;
import org.postgresql.util.PGobject;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class BeerRepository {
    private static final String SQL_SCRIPT_FOR_CREATE_NEW_BEER = "insert into beers (name, id_container, id_beer_type, alcohol_content, ibu, count_containers,date_create) values (?,?,?,?,?,?,?);";
    private static final String SQL_SCRIPT_FOR_CHANGE_BEER = "update beers set id_container = ?, count_containers = ?, date_change = ? where id = ?";
    private static final String SQL_FOR_GET_BEER_BY_ID = "select * from beers where id = ?";
    private static final String SQL_SCRIPT_FOR_GET_ALL_BEERS_WITH_LIMIT = "select * from beers limit ?";
    private static final String SQL_SCRIPT_FOR_GET_ALL_BEER_CONTAINERS = "select * from  beer_containers";
    private static final String SQL_SCRIPT_FOR_GET_ALL_BEER_TYPES = "select * from  beer_types";
    private static final String SQL_SCRIPT_FOR_GET_BEER_BY_ID_WITH_COUNT = "select id, name, id_container, id_beer_type, alcohol_content, ibu, count_containers from  beers where id = ?";
    private static final String SQL_FOR_ADD_NEW_BEER_ID = "or id =?";
    private static final String SQL_FOR_GET_BEER_TYPE_ID_BY_NAME = "select * from beer_types where type_name = ?";
    private static final String SQL_FOR_GET_BEER_CONTAINER_ID_BY_NAME_AND_VOLUME = "select * from beer_containers where name_type = ? and volume = ?";
    private static final String SQL_FOR_CHECK_IF_BEER_EXIST = "select * from beers where name = ? and id_container = ? and id_beer_type = ? and alcohol_content = ? and ibu = ?";
    private static final String SQL_FOR_CHECK_IF_BEER_EXIST_BY_ID = "select * from beers where id = ?";

    private final DataSource dataSource;

    public void save(BeerDto beerDto, LocalDateTime date) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_SCRIPT_FOR_CREATE_NEW_BEER)) {
            PGobject jsonObject = new PGobject();
            jsonObject.setType("json");
            jsonObject.setValue(beerDto.getCountBeer());
            preparedStatement.setString(1, beerDto.getName());
            preparedStatement.setInt(2, beerDto.getIdContainer());
            preparedStatement.setInt(3, beerDto.getIdBeerType());
            preparedStatement.setFloat(4, beerDto.getAlcoholContent());
            preparedStatement.setInt(5, beerDto.getIbu());
            preparedStatement.setObject(6, jsonObject);
            preparedStatement.setTimestamp(7, Timestamp.valueOf(date));

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

    public Beer getBeerById(int id) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_FOR_GET_BEER_BY_ID)) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();

            return Beer.
                    builder().
                    id(resultSet.getInt("id")).
                    name(resultSet.getString("name")).
                    idContainer(resultSet.getInt("id_container")).
                    idTypeBeer(resultSet.getInt("id_beer_type")).
                    alcoholContent(resultSet.getFloat("alcohol_content")).
                    ibu(resultSet.getInt("ibu")).
                    count(resultSet.getString("count_containers")).
                    build();
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
                    count(resultSet.getString("count_containers")).
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

    public boolean isBeerTypeExist(String name) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_FOR_GET_BEER_TYPE_ID_BY_NAME)) {
            statement.setString(1, name);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        }
    }

    public int getBeerTypeId(String name) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_FOR_GET_BEER_TYPE_ID_BY_NAME)) {
            statement.setString(1, name);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            return resultSet.getInt("id");
        }
    }

    public boolean isBeerContainerExist(String name, float volume) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_FOR_GET_BEER_CONTAINER_ID_BY_NAME_AND_VOLUME)) {
            statement.setString(1, name);
            statement.setFloat(2, volume);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        }
    }

    public int getBeerContainerId(String name, float volume) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_FOR_GET_BEER_CONTAINER_ID_BY_NAME_AND_VOLUME)) {
            statement.setString(1, name);
            statement.setFloat(2, volume);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            return resultSet.getInt("id");
        }
    }

    public boolean isBeerExist(BeerDto beerDto) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_FOR_CHECK_IF_BEER_EXIST)) {
            statement.setString(1, beerDto.getName());
            statement.setInt(2, beerDto.getIdContainer());
            statement.setInt(3, beerDto.getIdBeerType());
            statement.setFloat(4, beerDto.getAlcoholContent());
            statement.setInt(5, beerDto.getIbu());
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        }
    }

    public boolean isBeerExist(int id) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_FOR_CHECK_IF_BEER_EXIST_BY_ID)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        }
    }

    public void changeBeer(Beer beerDto, LocalDateTime date) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_SCRIPT_FOR_CHANGE_BEER)) {
            PGobject jsonObject = new PGobject();
            jsonObject.setType("json");
            jsonObject.setValue(beerDto.getCount());

            preparedStatement.setInt(1, beerDto.getIdContainer());
            preparedStatement.setObject(2, jsonObject);
            preparedStatement.setTimestamp(3, Timestamp.valueOf(date));
            preparedStatement.setInt(4, beerDto.getId());
            preparedStatement.execute();

            connection.commit();
        }
    }
}

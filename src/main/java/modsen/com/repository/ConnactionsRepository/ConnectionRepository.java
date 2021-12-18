package modsen.com.repository.ConnactionsRepository;

import modsen.com.service.PropertysService.PropertyFileReaderService;
import modsen.com.service.PropertysService.ReaderPropertyFile;
import org.postgresql.Driver;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionRepository {
    public static final ConnectionRepository CONNECTION_REPOSITORY = new ConnectionRepository();
    private String pathToDatabase;
    private final String login = "postgres";
    private final String password = "22k67s4A";

    public ConnectionRepository() {
        ReaderPropertyFile readerPropertyFile = new PropertyFileReaderService();
        try {
            this.pathToDatabase = readerPropertyFile.read("D:\\Java\\JavaProjects\\projects\\Modsen\\beerShop\\src\\main\\resources\\config\\config.properties", "pathToDatabase");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() throws SQLException {
        Driver driver = new Driver();
        return DriverManager.getConnection(pathToDatabase,login,password);
    }

    public static ConnectionRepository getInstance() {
        return CONNECTION_REPOSITORY;
    }
}

package modsen.com.repository.ConnactionsRepository;

import modsen.com.service.PropertysService.PropertyFileReaderService;
import modsen.com.service.PropertysService.ReaderPropertyFile;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionRepository {
    private static final ConnectionRepository CONNECTION_REPOSITORY= new ConnectionRepository();
    private final String pathToDatabase;
    private final String login;
    private final String password;
    private final String filePath = "./src/main/resources/config/config.properties";

    public ConnectionRepository() {
        ReaderPropertyFile readerPropertyFile = new PropertyFileReaderService();
        this.login = readerPropertyFile.read(filePath,"JDBC.login");
        this.password = readerPropertyFile.read(filePath,"JDBC.password");
        this.pathToDatabase = readerPropertyFile.read(filePath, "JDBC.path");
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(pathToDatabase,login,password);
    }

    public static ConnectionRepository getInstance() {
        return CONNECTION_REPOSITORY;
    }
}

package modsen.com.repository.connections;

import modsen.com.service.property.PropertyFileReaderService;
import modsen.com.service.property.ReaderPropertyFile;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionRepositoryImpl implements ConnectionRepository{
    private final String pathToDatabase;
    private final String login;
    private final String password;
    private final String fileName = "config/config.properties";

    public ConnectionRepositoryImpl() {
        String filePath = this.getClass().getClassLoader().getResource(fileName).getPath();
        ReaderPropertyFile readerPropertyFile = new PropertyFileReaderService();
        this.login = readerPropertyFile.read(filePath,"JDBC.login");
        this.password = readerPropertyFile.read(filePath,"JDBC.password");
        this.pathToDatabase = readerPropertyFile.read(filePath, "JDBC.path");
    }

    public Connection connect() throws SQLException {
        return DriverManager.getConnection(pathToDatabase,login,password);
    }

}

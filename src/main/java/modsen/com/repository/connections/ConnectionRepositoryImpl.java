package modsen.com.repository.connections;

import modsen.com.service.Constants;
import modsen.com.service.property.PropertyFileReaderService;
import modsen.com.service.property.ReaderPropertyFile;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionRepositoryImpl implements ConnectionRepository{
    private final String pathToDatabase;
    private final String login;
    private final String password;
    private final String filePath = Constants.CONFIG_FILE.getInfo();

    public ConnectionRepositoryImpl() {
        ReaderPropertyFile readerPropertyFile = new PropertyFileReaderService();
        this.login = readerPropertyFile.read(filePath,"JDBC.login");
        this.password = readerPropertyFile.read(filePath,"JDBC.password");
        this.pathToDatabase = readerPropertyFile.read(filePath, "JDBC.path");
    }

    public Connection connect() throws SQLException {
        return DriverManager.getConnection(pathToDatabase,login,password);
    }

}

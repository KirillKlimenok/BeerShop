package modsen.com.repository.connections;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
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
    private HikariDataSource hikari;

    public ConnectionRepositoryImpl() {
        String filePath = this.getClass().getClassLoader().getResource(fileName).getPath();
        ReaderPropertyFile readerPropertyFile = new PropertyFileReaderService();
        this.login = readerPropertyFile.read(filePath,"JDBC.login");
        this.password = readerPropertyFile.read(filePath,"JDBC.password");
        this.pathToDatabase = readerPropertyFile.read(filePath, "JDBC.path");
        config();
    }

    public Connection connect() throws SQLException {
        return hikari.getConnection();
    }

    private void config(){
        this.hikari = new HikariDataSource();
        this.hikari.setJdbcUrl(pathToDatabase);
        this.hikari.setUsername(login);
        this.hikari.setPassword(password);

    }

}

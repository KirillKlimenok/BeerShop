package modsen.com.repository.ConnactionsRepository;

import java.sql.Connection;
import java.sql.SQLException;

public interface ConnectionRepository {
    Connection connect() throws SQLException;
}

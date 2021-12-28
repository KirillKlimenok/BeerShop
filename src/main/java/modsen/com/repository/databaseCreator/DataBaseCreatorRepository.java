package modsen.com.repository.databaseCreator;

import java.io.FileNotFoundException;
import java.sql.SQLException;

public interface DataBaseCreatorRepository {
    boolean create() throws SQLException, FileNotFoundException;
}

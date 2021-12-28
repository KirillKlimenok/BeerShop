package modsen.com.repository.databaseCreator;

import java.io.FileNotFoundException;
import java.sql.SQLException;

public class DataBaseCreatorRepositoryImpl implements DataBaseCreatorRepository {
    @Override
    public boolean create() throws SQLException, FileNotFoundException {
        return true;
    }
}

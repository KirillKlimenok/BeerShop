package com.modsen.repository.connections;

import java.sql.Connection;
import java.sql.SQLException;

public interface ConnectionRepository {
    Connection connect() throws SQLException;
}

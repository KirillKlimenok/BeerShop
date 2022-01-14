package com.modsen.service;

import com.modsen.exception.AccessException;

import java.sql.SQLException;
import java.util.UUID;

public interface AdminActionService {
    void addNewPosition();

    void checkIsAdmin(UUID token) throws SQLException, AccessException;
}

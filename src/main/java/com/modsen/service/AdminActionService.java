package com.modsen.service;

import com.modsen.exception.AccessException;
import com.modsen.сontroller.model.BeerResponse;

import java.sql.SQLException;
import java.util.UUID;

public interface AdminActionService {
    void addNewPosition(BeerResponse beerResponse);

    void checkIsAdmin(UUID token) throws SQLException, AccessException;
}

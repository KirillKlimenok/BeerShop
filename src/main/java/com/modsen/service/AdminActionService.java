package com.modsen.service;

import com.modsen.exception.AccessException;
import com.modsen.exception.BeerNotFoundException;
import com.modsen.exception.BeerParameterNotExistException;
import com.modsen.exception.DataAlreadyCreatedException;
import com.modsen.repository.entytie.Beer;
import com.modsen.service.dto.BeerDto;
import com.modsen.сontroller.model.AdminTransactionRequest;
import com.modsen.сontroller.model.AdminTransactionResponse;
import com.modsen.сontroller.model.BeerResponse;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public interface AdminActionService {
    void addNewPosition(BeerResponse beerResponse) throws SQLException, BeerParameterNotExistException, DataAlreadyCreatedException;

    Beer changePosition(Beer beerDto) throws SQLException, BeerParameterNotExistException, BeerNotFoundException;

    void checkIsAdmin(UUID token) throws SQLException, AccessException;

    List<AdminTransactionResponse> getTransactions(AdminTransactionRequest transactionRequest) throws SQLException;
}

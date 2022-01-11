package com.modsen.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.modsen.exception.BeerNotFoundException;
import com.modsen.exception.TransactionException;
import com.modsen.exception.TransactionNotFoundException;
import com.modsen.exception.UserNotFoundException;
import com.modsen.сontroller.model.BeerRequest;
import com.modsen.сontroller.model.BeerResponse;
import com.modsen.сontroller.model.BeerTransactionRequest;
import com.modsen.сontroller.model.UserTransactionRequest;
import com.modsen.сontroller.model.UserTransactionResponse;

import java.sql.SQLException;
import java.util.List;

public interface UserActionService {
    void buyBeer(List<BeerRequest> beerId, String userToken) throws SQLException, UserNotFoundException, BeerNotFoundException, JsonProcessingException, TransactionException;
    List<UserTransactionResponse> getTransactions(UserTransactionRequest userTransactionRequest) throws SQLException, TransactionNotFoundException, UserNotFoundException;
    List<BeerResponse> getListBeers(BeerTransactionRequest userTransactionRequest) throws SQLException, TransactionNotFoundException, UserNotFoundException, BeerNotFoundException, JsonProcessingException;
}

package com.modsen.service;

import com.modsen.exception.BeerNotFoundException;
import com.modsen.exception.TransactionNotFoundException;
import com.modsen.exception.UserNotFoundException;
import com.modsen.сontroller.model.BeerRequest;
import com.modsen.сontroller.model.TransactionRequest;
import com.modsen.сontroller.model.TransactionResponse;

import java.sql.SQLException;
import java.util.List;

public interface UserActionService {
    void buyBeer(List<BeerRequest> beerId, String userToken) throws SQLException, UserNotFoundException, BeerNotFoundException;
    List<TransactionResponse> getTransactions(TransactionRequest transactionRequest) throws SQLException, TransactionNotFoundException, UserNotFoundException;
}

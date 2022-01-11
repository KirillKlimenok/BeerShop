package com.modsen.service;

import com.modsen.exception.BeerNotFoundException;
import com.modsen.exception.TransactionException;
import com.modsen.exception.UserNotFoundException;
import com.modsen.exception.UserRegistrationException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

public interface UserDoPostService {
    void apply(HttpServletRequest userToken, HttpServletResponse response, String bodyRequest) throws IOException, SQLException, UserRegistrationException, UserNotFoundException, BeerNotFoundException, TransactionException;

    String getUrl();
}

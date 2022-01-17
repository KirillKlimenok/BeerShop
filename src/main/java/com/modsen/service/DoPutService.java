package com.modsen.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.modsen.exception.AccessException;
import com.modsen.exception.BeerNotFoundException;
import com.modsen.exception.BeerParameterNotExistException;
import com.modsen.exception.DataAlreadyCreatedException;
import com.modsen.exception.TransactionException;
import com.modsen.exception.UserNotFoundException;
import com.modsen.exception.UserRegistrationException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

public interface DoPutService {
    void apply(HttpServletRequest userToken, HttpServletResponse response, String bodyRequest) throws AccessException, SQLException, IOException, BeerNotFoundException, BeerParameterNotExistException;

    String getUrl();
}

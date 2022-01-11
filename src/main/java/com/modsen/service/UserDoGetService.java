package com.modsen.service;

import com.modsen.exception.BeerNotFoundException;
import com.modsen.exception.TransactionNotFoundException;
import com.modsen.exception.UserNotFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

public interface UserDoGetService {
    void apply(HttpServletRequest request, HttpServletResponse response, String bodyRequest) throws IOException, UserNotFoundException, BeerNotFoundException, SQLException, TransactionNotFoundException;

    String getUrl();
}

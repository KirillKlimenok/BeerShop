package com.modsen.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.modsen.exception.TransactionNotFoundException;
import com.modsen.exception.UserNotFoundException;
import com.modsen.сontroller.model.UserTransactionRequest;
import lombok.Builder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

@Builder
public class TransactionDoGetServiceImpl implements DoGetService {
    private static final String URL_REQUEST = "/beerShop/user/get-user-transactions";
    private UserActionService userActionService;
    private ObjectMapper objectMapper;
    private String nameHeaderToken;

    @Override
    public void apply(HttpServletRequest request, HttpServletResponse response, String bodyRequest) throws IOException, UserNotFoundException, SQLException, TransactionNotFoundException {
        String token = request.getHeader(nameHeaderToken);
        UserTransactionRequest userTransactionRequest = objectMapper.readValue(bodyRequest, UserTransactionRequest.class);
        userTransactionRequest.setUserToken(token);
        String userTransactionsJson = objectMapper.writeValueAsString(userActionService.getTransactions(userTransactionRequest));

        response.setStatus(200);
        response.setContentType("application/json;Windows-1251;charset=utf-8");
        PrintWriter printWriter = response.getWriter();
        printWriter.println(userTransactionsJson);
        printWriter.close();
    }

    @Override
    public String getUrl() {
        return URL_REQUEST;
    }
}

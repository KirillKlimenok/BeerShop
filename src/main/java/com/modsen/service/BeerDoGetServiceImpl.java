package com.modsen.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.modsen.exception.BeerNotFoundException;
import com.modsen.exception.TransactionNotFoundException;
import com.modsen.exception.UserNotFoundException;
import com.modsen.сontroller.model.BeerTransactionRequest;
import lombok.Builder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

@Builder
public class BeerDoGetServiceImpl implements DoGetService {
    private static final String URL_REQUEST = "/beerShop/user/get-beer-list";
    private UserActionService userActionService;
    private ObjectMapper objectMapper;
    private String nameAccessToken;


    @Override
    public void apply(HttpServletRequest request, HttpServletResponse response, String bodyRequest) throws IOException, UserNotFoundException, BeerNotFoundException, SQLException, TransactionNotFoundException {
        BeerTransactionRequest beerTransactionRequest = objectMapper.readValue(bodyRequest, BeerTransactionRequest.class);
        beerTransactionRequest.setUserToken(request.getHeader(nameAccessToken));
        String beersListJson = objectMapper.writeValueAsString(userActionService.getListBeers(beerTransactionRequest));

        response.setStatus(200);
        response.setContentType("application/json;Windows-1251;charset=utf-8");
        PrintWriter printWriter = response.getWriter();
        printWriter.println(beersListJson);
        printWriter.close();
    }

    @Override
    public String getUrl() {
        return URL_REQUEST;
    }
}

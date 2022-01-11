package com.modsen.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.modsen.exception.BeerNotFoundException;
import com.modsen.exception.TransactionException;
import com.modsen.exception.UserNotFoundException;
import com.modsen.exception.UserRegistrationException;
import com.modsen.сontroller.model.BeerRequest;
import lombok.Builder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

@Builder
public class BuyBeerUserDoPostServiceImpl implements UserDoPostService {
    private static final String URL_REQUEST = "/beerShop/user/buy-beer";

    private UserActionService userActionService;
    private ObjectMapper objectMapper;
    private String nameHeaderToken;


    @Override
    public void apply(HttpServletRequest request, HttpServletResponse response, String bodyRequest) throws IOException, SQLException, UserNotFoundException, BeerNotFoundException, TransactionException {
        String token = request.getHeader(nameHeaderToken);
        List<BeerRequest> buysBeer = List.of(objectMapper.readValue(bodyRequest, BeerRequest[].class));
        userActionService.buyBeer(buysBeer, token);
        response.setStatus(200);
        PrintWriter printWriter = response.getWriter();
        printWriter.write("Done");
    }

    @Override
    public String getUrl() {
        return URL_REQUEST;
    }
}

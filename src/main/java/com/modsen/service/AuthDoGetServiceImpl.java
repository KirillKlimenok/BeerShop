package com.modsen.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.modsen.exception.BeerNotFoundException;
import com.modsen.exception.TransactionNotFoundException;
import com.modsen.exception.UserNotFoundException;
import com.modsen.сontroller.model.UserRequest;
import com.modsen.сontroller.model.UserResponse;
import lombok.Builder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@Builder
public class AuthDoGetServiceImpl implements UserDoGetService {
    private static final String URL_REQUEST = "/beerShop/auth";

    private ObjectMapper objectMapper;
    private RegistrationAndAuthService registrationAndAuthService;
    private String nameHeaderToken;

    @Override
    public void apply(HttpServletRequest request, HttpServletResponse response, String bodyRequest) throws IOException, UserNotFoundException, BeerNotFoundException, SQLException, TransactionNotFoundException {
        UserRequest user = objectMapper.readValue(bodyRequest, UserRequest.class);
        UserResponse userResponse = registrationAndAuthService.getTokenUser(user);

        response.setStatus(200);
        response.setContentType("text/html");
        response.setHeader(nameHeaderToken, userResponse.getToken());
    }

    @Override
    public String getUrl() {
        return URL_REQUEST;
    }
}

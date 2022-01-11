package com.modsen.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.modsen.exception.UserRegistrationException;
import com.modsen.сontroller.model.UserRequest;
import lombok.Builder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

@Builder
public class SingUpUserDoPostServiceImpl implements UserDoPostService {
    private static final String URL_REQUEST = "/beerShop/signup";

    private RegistrationAndAuthService registrationAndAuthService;
    private ObjectMapper objectMapper;


    @Override
    public void apply(HttpServletRequest request, HttpServletResponse response, String bodyRequest) throws IOException, SQLException, UserRegistrationException {
        UserRequest user = objectMapper.readValue(bodyRequest, UserRequest.class);
        registrationAndAuthService.createNewUser(user);
        response.setStatus(200);
        PrintWriter printWriter = response.getWriter();
        printWriter.write("done");
        printWriter.close();
    }

    @Override
    public String getUrl() {
        return URL_REQUEST;
    }
}

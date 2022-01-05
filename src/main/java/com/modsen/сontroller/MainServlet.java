package com.modsen.сontroller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.modsen.entitys.dto.UnregisteredUserDto;
import com.modsen.exceptions.NotTrueValidationUserException;
import com.modsen.repository.user.UserRepository;
import com.modsen.service.UserService;
import com.modsen.service.EmailValidatorService;
import com.modsen.service.LoginValidatorService;
import com.modsen.service.ValidationsService;
import com.modsen.service.ValidationsServiceImpl;
import com.modsen.service.Validator;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.log4j.Log4j;
import com.modsen.service.UserServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@WebServlet(name = "MainServlet", value = "/**")
@Log4j
public class MainServlet extends HttpServlet {
    private ObjectMapper objectMapper;
    private UserService userService;
    private ValidationsService validationsService;
    private List<Validator> validators;
    private HikariDataSource hikariDataSource;
    private UserRepository userRepository;
    private static final String FILE_PROPERTY = "config/dataBaseConfig.properties";


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UnregisteredUserDto user = objectMapper.readValue(getBodyReq(request), UnregisteredUserDto.class);
        String token = userService.getTokenUser(user);
        response.setStatus(200);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String jsonUnregUser = getBodyReq(request);
        try {
            UnregisteredUserDto user = objectMapper.readValue(jsonUnregUser, UnregisteredUserDto.class);
            if (validationsService.validate(user,validators)) {
                userService.createNewUser(user, validators);
                response.setStatus(200);
            } else {
                response.sendError(400, "wrong email or password");
            }
        } catch (JsonProcessingException | NotTrueValidationUserException e) {
            log.error(e.getMessage());
            response.sendError(400, "you entered wrong login or password\n");
        }

    }

    @Override
    public void init() {
        validationsService = new ValidationsServiceImpl();
        userRepository = new UserRepository(hikariDataSource.getDataSource());

        Properties properties = new Properties();
        try {
            properties.load(this.getClass().getClassLoader().getResourceAsStream(FILE_PROPERTY));
        } catch (IOException e) {
            log.error(e.getMessage());
        }

        hikariDataSource = new HikariDataSource(new HikariConfig(properties));
        userService = new UserServiceImpl(userRepository, validationsService);
        validators = new ArrayList<Validator>() {{
            add(new EmailValidatorService());
            add(new LoginValidatorService());
        }};
    }

    private String getBodyReq(HttpServletRequest req) throws IOException {
        BufferedReader bufferedReader = req.getReader();
        StringBuilder stringBuilder = new StringBuilder();
        String str;

        while ((str = bufferedReader.readLine()) != null) {
            stringBuilder.append(str).append("\n");
        }
        return stringBuilder.toString();
    }
}

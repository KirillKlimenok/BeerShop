package com.modsen.сontroller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.modsen.exception.UserNotFoundException;
import com.modsen.exception.PropertyNotFoundException;
import com.modsen.exception.UserRegistrationException;
import com.modsen.service.TokenService;
import com.modsen.сontroller.model.UserRequest;
import com.modsen.сontroller.model.UserResponse;
import com.modsen.exception.ValidationException;
import com.modsen.repository.user.UserRepository;
import com.modsen.service.EmailValidatorService;
import com.modsen.service.LoginValidatorService;
import com.modsen.service.UserService;
import com.modsen.service.UserServiceImpl;
import com.modsen.service.Validator;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.log4j.Log4j;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;
import java.util.function.Function;

@WebServlet(name = "MainServlet", value = "/*")
@Log4j
public class MainServlet extends HttpServlet {
    private ObjectMapper objectMapper;
    private UserService userService;
    private List<Validator<UserRequest>> validators;
    private HikariDataSource hikariDataSource;
    private UserRepository userRepository;
    private static final String FILE_PROPERTY = "config/dataBaseConfig.properties";
    private final Function<UserRequest, String> functionGetEmailUserRequest = UserRequest::getEmail;
    private final Function<UserRequest, String> functionGetLoginUserRequest = UserRequest::getLogin;
    private TokenService tokenService;
    private DataSource dataSource;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            UserRequest user = objectMapper.readValue(getBodyReq(request), UserRequest.class);
            UserResponse userResponse = userService.getTokenUser(user);

            response.setStatus(200);
            response.setContentType("text/html");
            PrintWriter printWriter = response.getWriter();
            printWriter.print(userResponse.getToken());
            printWriter.close();
        } catch (SQLException e) {
            log.error(e.getMessage());
            response.sendError(500, "User not found");
        } catch (IOException e) {
            log.error(e.getMessage());
            response.sendError(500, "Server Exception");
        } catch (UserNotFoundException e) {
            response.sendError(400, e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String jsonUser = getBodyReq(request);
        try {
            UserRequest user = objectMapper.readValue(jsonUser, UserRequest.class);

            userService.createNewUser(user);
            response.setStatus(200);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            response.sendError(500, "please try again" + e.getMessage());
        } catch (ValidationException e) {
            log.error(e.getMessage());
            response.sendError(400, e.getMessage());
        } catch (SQLException e) {
            log.error(e.getMessage());
            response.sendError(500, e.getMessage() + "\n please try again");
        } catch (UserRegistrationException e) {
            response.sendError(400, e.getMessage());
        }
    }

    @Override
    public void init() {
        EmailValidatorService<UserRequest> emailValidator = new EmailValidatorService(functionGetEmailUserRequest);
        LoginValidatorService<UserRequest> loginValidator = new LoginValidatorService(functionGetLoginUserRequest);
        validators = List.of(emailValidator, loginValidator);

        Properties properties = new Properties();
        loadProperties(properties);

        HikariConfig hikariConfig = new HikariConfig(properties);
        hikariConfig.setAutoCommit(false);
        hikariDataSource = new HikariDataSource(hikariConfig);

        dataSource = hikariDataSource;
        objectMapper = new ObjectMapper();
        tokenService = new TokenService();
        userRepository = new UserRepository(dataSource);
        userService = new UserServiceImpl(userRepository, validators, tokenService);
    }

    @Override
    public void destroy() {
        hikariDataSource.close();
        super.destroy();
    }

    private void loadProperties(Properties properties) {
        try {
            properties.load(this.getClass().getClassLoader().getResourceAsStream(FILE_PROPERTY));
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new PropertyNotFoundException(e.getMessage());
        }
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

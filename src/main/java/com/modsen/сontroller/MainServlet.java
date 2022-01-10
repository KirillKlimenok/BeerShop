package com.modsen.сontroller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.modsen.exception.BeerNotFoundException;
import com.modsen.exception.PropertyNotFoundException;
import com.modsen.exception.TransactionNotFoundException;
import com.modsen.exception.UserNotFoundException;
import com.modsen.exception.UserRegistrationException;
import com.modsen.exception.ValidationException;
import com.modsen.repository.BeerRepository;
import com.modsen.repository.TransactionRepository;
import com.modsen.repository.UserRepository;
import com.modsen.service.CountTransactionValidatorService;
import com.modsen.service.EmailValidatorService;
import com.modsen.service.LoginValidatorService;
import com.modsen.service.TokenService;
import com.modsen.service.UserActionService;
import com.modsen.service.UserActionServiceImpl;
import com.modsen.service.UserService;
import com.modsen.service.UserServiceImpl;
import com.modsen.service.Validator;
import com.modsen.сontroller.model.BeerRequest;
import com.modsen.сontroller.model.TransactionRequest;
import com.modsen.сontroller.model.TransactionResponse;
import com.modsen.сontroller.model.UserRequest;
import com.modsen.сontroller.model.UserResponse;
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
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Properties;
import java.util.function.Function;

@Log4j
@WebServlet(name = "MainServlet", value = "/*")
public class MainServlet extends HttpServlet {
    private ObjectMapper objectMapper;
    private UserService userService;
    private List<Validator<UserRequest>> userRequestValidators;
    private List<Validator<BeerRequest>> beerValidators;
    private List<Validator<TransactionRequest>> transactionsValidators;
    private HikariDataSource hikariDataSource;
    private UserRepository userRepository;
    private static final String FILE_PROPERTY_DATABASE_CONFIG = "config/dataBaseConfig.properties";
    private static final String FILE_PROPERTY_CONFIG = "config/config.properties";
    public static final String NAME_PROPERTY_WITH_MIN_COUNT_TRANSACTION = "minCountTransactionOnPage";
    public static final String NAME_PROPERTY_WITH_MAX_COUNT_TRANSACTION = "maxCountTransactionOnPage";
    public static final String NAME_ACCESS_TOKEN = "App-Auth";
    private final Function<UserRequest, String> functionGetEmailUserRequest = UserRequest::getEmail;
    private final Function<UserRequest, String> functionGetLoginUserRequest = UserRequest::getLogin;
    private final Function<TransactionRequest, Integer> functionGetCountTransactions = TransactionRequest::getCount;
    private TokenService tokenService;
    private BeerRepository beerRepository;
    private TransactionRepository transactionRepository;
    private UserActionService userActionService;
    private DataSource dataSource;
    private DateTimeFormatter dateTimeFormatter;
    private static final String DATE_PATTERN = "dd-MM-yyyy";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String userToken = request.getHeader(NAME_ACCESS_TOKEN);
            if (userToken == null) {
                try {
                    UserRequest user = objectMapper.readValue(getBodyReq(request), UserRequest.class);
                    UserResponse userResponse = userService.getTokenUser(user);

                    response.setStatus(200);
                    response.setContentType("text/html");
                    response.setHeader(NAME_ACCESS_TOKEN, userResponse.getToken());
                } catch (MismatchedInputException e) {
                    log.warn(e.getMessage());
                    response.sendError(400, "Please log in, before do any actions");
                }
            } else {
                String bodyRequest = getBodyReq(request);
                try (PrintWriter printWriter = response.getWriter()) {
                    TransactionRequest transactionRequest = objectMapper.readValue(bodyRequest, TransactionRequest.class);
                    transactionRequest.setUserToken(userToken);
                    List<TransactionResponse> transactionResponses = userActionService.getTransactions(transactionRequest);
                    response.setStatus(200);
                    printWriter.println(objectMapper.writeValueAsString(transactionResponses));
                } catch (UserNotFoundException e) {
                    log.warn(e.getMessage());
                    response.sendError(401, e.getMessage());
                } catch (TransactionNotFoundException e) {
                    log.warn(e.getMessage());
                    response.sendError(400, e.getMessage());
                } catch (MismatchedInputException e) {
                    log.error(e.getMessage());
                    response.sendError(400, "You entered wrong data. Please check data who you send");
                }
            }
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
        try {
            String userToken = request.getHeader(NAME_ACCESS_TOKEN);
            String bodyRequest = getBodyReq(request);
            if (userToken == null) {
                UserRequest user = objectMapper.readValue(bodyRequest, UserRequest.class);

                userService.createNewUser(user);
                response.setStatus(200);
            } else {
                try {
                    List<BeerRequest> buysBeer = List.of(objectMapper.readValue(bodyRequest, BeerRequest[].class));
                    userActionService.buyBeer(buysBeer, userToken);
                    response.setStatus(200);
                } catch (BeerNotFoundException e) {
                    response.sendError(400, e.getMessage());
                } catch (UserNotFoundException e) {
                    response.sendError(401, e.getMessage());
                }
            }
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
        Properties dataBaseProperty = new Properties();
        Properties configProperty = new Properties();
        loadProperties(dataBaseProperty, FILE_PROPERTY_DATABASE_CONFIG);
        loadProperties(configProperty, FILE_PROPERTY_CONFIG);

        int maxCountTransactions = Integer.parseInt(configProperty.getProperty(NAME_PROPERTY_WITH_MAX_COUNT_TRANSACTION).trim());
        int minCountTransactions = Integer.parseInt(configProperty.getProperty(NAME_PROPERTY_WITH_MIN_COUNT_TRANSACTION).trim());

        CountTransactionValidatorService<TransactionRequest> countTransactionValidatorService = new CountTransactionValidatorService(functionGetCountTransactions, maxCountTransactions, minCountTransactions);
        EmailValidatorService<UserRequest> emailValidator = new EmailValidatorService(functionGetEmailUserRequest);
        LoginValidatorService<UserRequest> loginValidator = new LoginValidatorService(functionGetLoginUserRequest);
        transactionsValidators = List.of(countTransactionValidatorService);
        userRequestValidators = List.of(emailValidator, loginValidator);
        beerValidators = List.of();


        HikariConfig hikariConfig = new HikariConfig(dataBaseProperty);
        hikariConfig.setAutoCommit(false);
        hikariDataSource = new HikariDataSource(hikariConfig);


        dataSource = hikariDataSource;
        dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_PATTERN);
        objectMapper = new ObjectMapper();
        tokenService = new TokenService();
        userRepository = new UserRepository(dataSource);
        userService = new UserServiceImpl(userRepository, userRequestValidators, tokenService);
        beerRepository = new BeerRepository(dataSource);
        transactionRepository = new TransactionRepository(dataSource);
        userActionService = new UserActionServiceImpl(beerRepository, userRepository, transactionRepository, beerValidators, transactionsValidators, dateTimeFormatter);
    }

    @Override
    public void destroy() {
        hikariDataSource.close();
        super.destroy();
    }

    private void loadProperties(Properties properties, String path) {
        try {
            properties.load(this.getClass().getClassLoader().getResourceAsStream(path));
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
/*
                   try (PrintWriter writer = response.getWriter()){
                        List<BeerRequest> beerRequest = List.of(objectMapper.readValue(bodyRequest, BeerRequest[].class));
                        userActionService.buyBeer(beerRequest, userToken);

                        response.setStatus(200);
                        writer.print("done");
                    } catch (MismatchedInputException e2) {
                        log.error(e2.getMessage());
                        response.sendError(500, e2.getMessage());
                    } catch (BeerNotFoundException beerNotFoundException) {
                        log.error(beerNotFoundException.getMessage());
                        response.sendError(400, beerNotFoundException.getMessage());
                    }
*/

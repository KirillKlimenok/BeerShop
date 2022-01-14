package com.modsen.сontroller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.modsen.exception.AccessException;
import com.modsen.exception.BeerNotFoundException;
import com.modsen.exception.PropertyNotFoundException;
import com.modsen.exception.TransactionException;
import com.modsen.exception.TransactionNotFoundException;
import com.modsen.exception.UserNotFoundException;
import com.modsen.exception.UserRegistrationException;
import com.modsen.exception.ValidationException;
import com.modsen.exception.WrongDataException;
import com.modsen.repository.BeerRepository;
import com.modsen.repository.TransactionRepository;
import com.modsen.repository.UserActionRepository;
import com.modsen.repository.UserRepository;
import com.modsen.service.*;
import com.modsen.сontroller.model.BeerRequest;
import com.modsen.сontroller.model.Transaction;
import com.modsen.сontroller.model.UserRequest;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.log4j.Log4j2;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Properties;

@Log4j2
@WebServlet(name = "MainServlet", value = "/*")
public class MainServlet extends HttpServlet {
    private ObjectMapper objectMapper;
    private RegistrationAndAuthService registrationAndAuthService;
    private List<Validator<UserRequest>> userRequestValidators;
    private List<Validator<BeerRequest>> beerValidators;
    private List<Validator<Transaction>> transactionsValidators;
    private List<DoGetService> doGetServices;
    private List<DoPostService> doPostServices;
    private HikariDataSource hikariDataSource;
    private UserRepository userRepository;
    private static final String FILE_PROPERTY_DATABASE_CONFIG = "config/dataBaseConfig.properties";
    private static final String FILE_PROPERTY_CONFIG = "config/config.properties";
    public static final String NAME_PROPERTY_WITH_MIN_COUNT_TRANSACTION = "minCountTransactionOnPage";
    public static final String NAME_PROPERTY_WITH_MAX_COUNT_TRANSACTION = "maxCountTransactionOnPage";
    public static final String NAME_ACCESS_TOKEN = "App-Auth";
    private TokenService tokenService;
    private BeerRepository beerRepository;
    private TransactionRepository transactionRepository;
    private UserActionService userActionService;
    private UserActionRepository userActionRepository;
    private DataSource dataSource;
    private DateTimeFormatter dateTimeFormatter;
    private static final String DATE_PATTERN = "dd-MM-yyyy HH:mm:ss";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String url = request.getRequestURI();
            DoGetService userDoGetService = null;
            for (DoGetService doGetService : doGetServices) {
                if (doGetService.getUrl().equals(url)) {
                    userDoGetService = doGetService;
                    break;
                }
            }
            if (userDoGetService == null) {
                response.sendError(404);
            } else {
                userDoGetService.apply(request, response, getBodyReq(request));
            }
        } catch (UserNotFoundException e) {
            response.sendError(401, e.getMessage());
        } catch (BeerNotFoundException | TransactionNotFoundException | ValidationException | WrongDataException e) {
            response.sendError(400, e.getMessage());
        } catch (SQLException e) {
            log.error(e.getMessage());
            response.sendError(500, "Please try again");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String url = request.getRequestURI();
            DoPostService doPostService = null;
            for (DoPostService doGetService : doPostServices) {
                if (doGetService.getUrl().equals(url)) {
                    doPostService = doGetService;
                    break;
                }
            }
            if (doPostService == null) {
                response.sendError(404);
            } else {
                doPostService.apply(request, response, getBodyReq(request));
            }
        } catch (JsonProcessingException | SQLException e) {
            log.error(e.getMessage());
            response.sendError(500, "please try again" + e.getMessage());
        } catch (UserRegistrationException e) {
            log.warn(e.getMessage());
            response.sendError(400, e.getMessage());
        } catch (UserNotFoundException | AccessException e) {
            response.sendError(401, e.getMessage());
        } catch (BeerNotFoundException | TransactionException | ValidationException e) {
            response.sendError(400, e.getMessage());
        }
    }

    @Override
    public void init() {
        Properties dataBaseProperty = new Properties();
        Properties configProperty = new Properties();
        loadProperties(dataBaseProperty, FILE_PROPERTY_DATABASE_CONFIG);
        loadProperties(configProperty, FILE_PROPERTY_CONFIG);

        int maxCountObjectsOnPage = Integer.parseInt(configProperty.getProperty(NAME_PROPERTY_WITH_MAX_COUNT_TRANSACTION).trim());
        int minCountObjectsOnPage = Integer.parseInt(configProperty.getProperty(NAME_PROPERTY_WITH_MIN_COUNT_TRANSACTION).trim());

        CountValidatorService<Transaction> countValidatorService = CountValidatorService.<Transaction>builder().
                functionGetCount(Transaction::getCountTransaction).
                setMaxValue((obj) -> obj.setCountTransaction(maxCountObjectsOnPage)).
                setMinValue((obj) -> obj.setCountTransaction(minCountObjectsOnPage)).
                max(maxCountObjectsOnPage).
                min(minCountObjectsOnPage).
                build();
        EmailValidatorService<UserRequest> emailValidator = new EmailValidatorService<>(UserRequest::getEmail);
        LoginValidatorService<UserRequest> loginValidator = new LoginValidatorService<>(UserRequest::getLogin);
        transactionsValidators = List.of(countValidatorService);
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
        registrationAndAuthService = new RegistrationAndAuthServiceImpl(userRepository, userRequestValidators, tokenService);
        beerRepository = new BeerRepository(dataSource);
        transactionRepository = new TransactionRepository(dataSource);
        userActionRepository = new UserActionRepository(dataSource);
        userActionService = UserActionServiceImpl.builder().
                beerRepository(beerRepository).
                userRepository(userRepository).
                transactionRepository(transactionRepository).
                userActionRepository(userActionRepository).
                beerValidators(beerValidators).
                transactionsValidator(transactionsValidators).
                dateTimeFormatter(dateTimeFormatter).
                objectMapper(objectMapper).
                build();

        BeerDoGetServiceImpl beerUserDoGetService = BeerDoGetServiceImpl.
                builder().
                userActionService(userActionService).
                objectMapper(objectMapper).
                nameAccessToken(NAME_ACCESS_TOKEN).
                build();

        AuthDoGetServiceImpl authDoGetService = AuthDoGetServiceImpl.
                builder().
                registrationAndAuthService(registrationAndAuthService).
                objectMapper(objectMapper).
                nameHeaderToken(NAME_ACCESS_TOKEN).
                build();

        TransactionDoGetServiceImpl transactionUserDoGetService = TransactionDoGetServiceImpl.
                builder().
                userActionService(userActionService).
                objectMapper(objectMapper).
                nameHeaderToken(NAME_ACCESS_TOKEN).
                build();

        SingUpDoPostServiceImpl singUpUserDoPostService = SingUpDoPostServiceImpl.
                builder().
                registrationAndAuthService(registrationAndAuthService).
                objectMapper(objectMapper).
                build();

        BuyBeerDoPostServiceImpl buyBeerUserDoPostService = BuyBeerDoPostServiceImpl.
                builder().
                userActionService(userActionService).
                objectMapper(objectMapper).
                nameHeaderToken(NAME_ACCESS_TOKEN).
                build();

        doGetServices = List.of(beerUserDoGetService, authDoGetService, transactionUserDoGetService);
        doPostServices = List.of(singUpUserDoPostService, buyBeerUserDoPostService);
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

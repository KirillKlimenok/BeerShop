package com.modsen.сontroller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.modsen.exception.*;
import com.modsen.repository.AdminRepository;
import com.modsen.repository.BeerRepository;
import com.modsen.repository.TransactionRepository;
import com.modsen.repository.UserActionRepository;
import com.modsen.repository.UserRepository;
import com.modsen.repository.entytie.Beer;
import com.modsen.service.*;
import com.modsen.сontroller.model.BeerRequest;
import com.modsen.сontroller.model.BeerResponse;
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
import java.util.Arrays;
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
    private List<Validator<BeerResponse>> beerResponseValidators;
    private List<DoGetService> doGetServices;
    private List<DoPostService> doPostServices;
    private List<DoPutService> doPutServices;
    private HikariDataSource hikariDataSource;
    private UserRepository userRepository;
    private static final String FILE_PROPERTY_DATABASE_CONFIG = "config/dataBaseConfig.properties";
    private static final String FILE_PROPERTY_CONFIG = "config/config.properties";
    public static final String NAME_PROPERTY_WITH_MIN_COUNT_TRANSACTION = "minCountTransactionOnPage";
    public static final String NAME_PROPERTY_WITH_MAX_COUNT_TRANSACTION = "maxCountTransactionOnPage";
    public static final String NAME_PROPERTY_WITH_MAX_ALCOHOL_CONTENT = "beer.maxPercentageAlcoholContent";
    public static final String NAME_PROPERTY_WITH_MIN_ALCOHOL_CONTENT = "beer.minPercentageAlcoholContent";
    public static final String NAME_PROPERTY_WITH_MAX_VOLUME = "beer.maxContainerVolume";
    public static final String NAME_PROPERTY_WITH_MIN_VOLUME = "beer.minContainerVolume";
    public static final String NAME_PROPERTY_WITH_MAX_IBU = "beer.maxIbu";
    public static final String NAME_PROPERTY_WITH_MIN_IBU = "beer.minIbu";
    public static final String NAME_ACCESS_TOKEN = "App-Auth";
    private List<String> nameContainersWithoutConditions;
    private TokenService tokenService;
    private BeerRepository beerRepository;
    private TransactionRepository transactionRepository;
    private UserActionService userActionService;
    private UserActionRepository userActionRepository;
    private AdminActionService adminActionService;
    private AdminRepository adminRepository;
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
        } catch (UserNotFoundException | AccessException e) {
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
            request.setCharacterEncoding("utf-8");
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
        } catch (UserRegistrationException | BeerParameterNotExistException e) {
            response.setContentType("application/json;Windows-1251;charset=utf-8");
            log.warn(e.getMessage());
            response.sendError(400, e.getMessage());
        } catch (UserNotFoundException | AccessException e) {
            response.sendError(401, e.getMessage());
        } catch (BeerNotFoundException | TransactionException | ValidationException | DataAlreadyCreatedException e) {
            response.sendError(400, e.getMessage());
        } catch (JsonProcessingException | SQLException e) {
            log.error(e.getMessage());
            response.sendError(500, "please try again");
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            request.setCharacterEncoding("utf-8");
            String url = request.getRequestURI();
            DoPutService doPostService = null;
            for (DoPutService doPutService : doPutServices) {
                if (doPutService.getUrl().equals(url)) {
                    doPostService = doPutService;
                    break;
                }
            }
            if (doPostService == null) {
                response.sendError(404);
            } else {
                doPostService.apply(request, response, getBodyReq(request));
            }
        } catch (BeerParameterNotExistException e) {
            response.setContentType("application/json;Windows-1251;charset=utf-8");
            log.warn(e.getMessage());
            response.sendError(400, e.getMessage());
        } catch (AccessException e) {
            response.sendError(401, e.getMessage());
        } catch (BeerNotFoundException | ValidationException e) {
            response.sendError(400, e.getMessage());
        } catch (JsonProcessingException | SQLException e) {
            log.error(e.getMessage() + Arrays.toString(e.getStackTrace()));
            response.sendError(500, Arrays.toString(e.getStackTrace()));
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
        float minBeerAlcoholContent = Float.parseFloat(configProperty.getProperty(NAME_PROPERTY_WITH_MIN_ALCOHOL_CONTENT).trim());
        float maxBeerAlcoholContent = Float.parseFloat(configProperty.getProperty(NAME_PROPERTY_WITH_MAX_ALCOHOL_CONTENT).trim());
        float minBeerVolume = Float.parseFloat(configProperty.getProperty(NAME_PROPERTY_WITH_MIN_VOLUME).trim());
        float maxBeerVolume = Float.parseFloat(configProperty.getProperty(NAME_PROPERTY_WITH_MAX_VOLUME).trim());
        int maxIbu = Integer.parseInt(configProperty.getProperty(NAME_PROPERTY_WITH_MAX_IBU).trim());
        int minIbu = Integer.parseInt(configProperty.getProperty(NAME_PROPERTY_WITH_MIN_IBU).trim());

        nameContainersWithoutConditions = List.of("разливное");

        CountValidatorService<Transaction> countValidatorService = CountValidatorService.
                <Transaction>builder().
                functionGetCount(Transaction::getCountTransaction).
                setMaxValue((obj) -> obj.setCountTransaction(maxCountObjectsOnPage)).
                setMinValue((obj) -> obj.setCountTransaction(minCountObjectsOnPage)).
                max(maxCountObjectsOnPage).
                min(minCountObjectsOnPage).
                build();
        EmailValidatorService<UserRequest> emailValidator = new EmailValidatorService<>(UserRequest::getEmail);
        LoginValidatorService<UserRequest> loginValidator = new LoginValidatorService<>(UserRequest::getLogin);
        BeerAlcoholValidatorImpl<BeerResponse> alcoholValidator = new BeerAlcoholValidatorImpl<>(BeerResponse::getAlcoholContent, minBeerAlcoholContent, maxBeerAlcoholContent);
        BeerVolumeValidatorService<BeerResponse> volumeAndContainerValidator = BeerVolumeValidatorService.
                <BeerResponse>builder().
                getType(BeerResponse::getContainer).
                getVolume(BeerResponse::getVolume).
                nameContainerBeerWithoutConditions(nameContainersWithoutConditions).
                max(maxBeerVolume).
                min(minBeerVolume)
                .build();

        BeerIbuValidatorService<BeerResponse> ibuValidator = BeerIbuValidatorService.
                <BeerResponse>builder().
                getIbu(BeerResponse::getIbu).
                max(maxIbu).
                min(minIbu).
                build();

        BeerTextFieldValidatorService<BeerResponse> nameValidator = new BeerTextFieldValidatorService<>(BeerResponse::getName, "name");
        BeerTextFieldValidatorService<BeerResponse> beerTypeValidator = new BeerTextFieldValidatorService<>(BeerResponse::getBeerType, "type");

        transactionsValidators = List.of(countValidatorService);
        userRequestValidators = List.of(emailValidator, loginValidator);
        beerValidators = List.of();
        beerResponseValidators = List.of(alcoholValidator, volumeAndContainerValidator, ibuValidator, nameValidator, beerTypeValidator);

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
        adminRepository = new AdminRepository(dataSource);
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
        adminActionService = new AdminActionServiceImpl(adminRepository, beerRepository, dateTimeFormatter, transactionRepository);

        BeerCountValidatorService<Beer> beerCountValidator = BeerCountValidatorService.
                <Beer>builder().
                getCount(Beer::getCount).
                objectMapper(objectMapper).
                parameter("count").
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

        AddNewBeerPositionDoPostServiceImpl addNewBeerPositionDoPostService = AddNewBeerPositionDoPostServiceImpl.
                builder().
                adminActionService(adminActionService).
                objectMapper(objectMapper).
                nameHeaderToken(NAME_ACCESS_TOKEN).
                validators(beerResponseValidators).
                build();

        ChangeBeerPositionDoPutService changeBeerPositionDoPutService = ChangeBeerPositionDoPutService.
                builder().
                adminActionService(adminActionService).
                nameHeaderToken(NAME_ACCESS_TOKEN).
                objectMapper(objectMapper).
                countValidator(beerCountValidator).
                build();
        GetAllUsersTransactionsDoGetService getAllUsersTransactionsDoGetService = GetAllUsersTransactionsDoGetService.
                builder().
                adminActionService(adminActionService).
                nameHeaderToken(NAME_ACCESS_TOKEN).
                objectMapper(objectMapper).
                validator(transactionsValidators).
                build();

        doGetServices = List.of(beerUserDoGetService, authDoGetService, transactionUserDoGetService, getAllUsersTransactionsDoGetService);
        doPostServices = List.of(singUpUserDoPostService, buyBeerUserDoPostService, addNewBeerPositionDoPostService);
        doPutServices = List.of(changeBeerPositionDoPutService);
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

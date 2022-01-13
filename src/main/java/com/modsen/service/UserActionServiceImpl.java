package com.modsen.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.modsen.exception.BeerNotFoundException;
import com.modsen.exception.BeerValidationException;
import com.modsen.exception.TransactionException;
import com.modsen.exception.TransactionNotFoundException;
import com.modsen.exception.UserNotFoundException;
import com.modsen.exception.WrongDataException;
import com.modsen.repository.BeerRepository;
import com.modsen.repository.TransactionRepository;
import com.modsen.repository.UserActionRepository;
import com.modsen.repository.UserRepository;
import com.modsen.repository.entytie.Beer;
import com.modsen.repository.entytie.BeerContainer;
import com.modsen.repository.entytie.BeerType;
import com.modsen.repository.entytie.UserTransactions;
import com.modsen.service.dto.TransactionDto;
import com.modsen.сontroller.model.BeerRequest;
import com.modsen.сontroller.model.BeerResponse;
import com.modsen.сontroller.model.BeerTransactionRequest;
import com.modsen.сontroller.model.Transaction;
import com.modsen.сontroller.model.UserTransactionRequest;
import com.modsen.сontroller.model.UserTransactionResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@AllArgsConstructor
@Builder
public class UserActionServiceImpl implements UserActionService {
    private BeerRepository beerRepository;
    private UserRepository userRepository;
    private TransactionRepository transactionRepository;
    private UserActionRepository userActionRepository;
    private List<Validator<BeerRequest>> beerValidators;
    private List<Validator<Transaction>> transactionsValidator;
    private DateTimeFormatter dateTimeFormatter;
    private ObjectMapper objectMapper;

    @Override
    public void buyBeer(List<BeerRequest> listBeersRequest, String userToken) throws SQLException, UserNotFoundException, BeerNotFoundException, JsonProcessingException, TransactionException {
        if (!userRepository.isUserExist(UUID.fromString(userToken))) {
            throw new UserNotFoundException("User with this " + userToken + " token not found. please register before by purchase");
        }

        listBeersRequest.forEach(beer -> beerValidators.forEach(validator -> validator.check(beer)));

        List<Integer> beerRequestId = listBeersRequest.
                stream().
                map(BeerRequest::getIdBeer).
                collect(Collectors.toList());

        List<Beer> beersInBd = beerRepository.getBeerByIds(beerRequestId);

        List<Integer> idsBeerFormBdResponse = beersInBd.
                stream().
                map(Beer::getId).
                collect(Collectors.toList());

        List<BeerRequest> newBeerCountInDb = new ArrayList<>();

        if (beersInBd.isEmpty()) {
            throw new BeerNotFoundException("List beer is clear");
        }

        if (!listBeersRequest.stream().allMatch(x -> idsBeerFormBdResponse.contains(x.getIdBeer()))) {
            throw new BeerNotFoundException("Please enter only existing positions");
        }

        setNewCountBeer(listBeersRequest, beersInBd, newBeerCountInDb);
        checkBeerRequest(beersInBd, listBeersRequest);

        LocalDateTime date = LocalDateTime.now();
        List<TransactionDto> transactionDtoList = new ArrayList<>();
        for (BeerRequest beerRequest : listBeersRequest) {
            int idBeer = beerRequest.getIdBeer();
            double countBeer = objectMapper.
                    readTree(beerRequest.getCountJson()).
                    get("count").
                    asDouble();

            TransactionDto transaction = new TransactionDto(idBeer, countBeer, date);
            transactionDtoList.add(transaction);
        }

        userActionRepository.updateCountBeerAndSaveTransactions(newBeerCountInDb, transactionDtoList, userToken);
    }

    private void checkBeerRequest(List<Beer> beersInBd, List<BeerRequest> listBeersRequest) throws SQLException, JsonProcessingException {
        Map<Integer, BeerContainer> beerContainerList = beerRepository.getBeerContainers();

        for (BeerRequest beerRequest : listBeersRequest) {
            String countRequestBeer = objectMapper.
                    readTree(beerRequest.getCountJson()).
                    get("count").
                    asText();

            int idContainerBeerInBd = beersInBd.
                    stream().
                    filter(x -> x.getId() == beerRequest.getIdBeer()).
                    findFirst().
                    get().
                    getIdContainer();

            String nameContainer = beerContainerList.
                    get(idContainerBeerInBd).
                    getName();

            if (!nameContainer.equalsIgnoreCase("разливное")) {
                try {
                    Integer.parseInt(countRequestBeer);
                } catch (NumberFormatException e) {
                    throw new BeerValidationException("When choosing a non-draft type of beer, you must specify only an integer value of the quantity");
                }
            }

        }

    }

    private void setNewCountBeer(List<BeerRequest> listBeersRequest, List<Beer> beersInBd, List<BeerRequest> newBeerCountInDb) throws TransactionException {
        for (int i = 0; i < beersInBd.size(); i++) {
            int beerId = listBeersRequest.get(i).getIdBeer();
            double countById = listBeersRequest.
                    stream().
                    filter(x -> x.getIdBeer() == beerId).
                    mapToDouble(x -> getValueCountFromJson(x.getCountJson())).
                    sum();
            if (countById < 0) {
                throw new TransactionException("You have entered a negative quantity of the desired product");
            }
            Beer beerInDb = beersInBd.stream().filter(x -> x.getId() == beerId).collect(Collectors.toList()).get(0);
            double currentCountBeer = getValueCountFromJson(beerInDb.getCountBeerJson());
            if (countById >= currentCountBeer) {
                throw new TransactionException("Do you want to buy beer id: " + beerId + " quantity " + countById + "?. Is there in stock: " + currentCountBeer + " such beer. Please correct the amount of beer you have selected or change the position");
            }
            String currentCountBeerJson = "{\"count\":" + (currentCountBeer - countById) + "}";
            newBeerCountInDb.add(new BeerRequest(beerId, currentCountBeerJson));
        }
    }

    @Override
    public List<UserTransactionResponse> getTransactions(UserTransactionRequest userTransactionRequest) throws SQLException, TransactionNotFoundException, UserNotFoundException {
        if (!userRepository.isUserExist(UUID.fromString(userTransactionRequest.getUserToken()))) {
            throw new UserNotFoundException("User with this " + userTransactionRequest.getUserToken() + " token not found. please register before by purchase");
        }

        transactionsValidator.forEach(x -> x.check(userTransactionRequest));


        List<UserTransactions> userTransactions = transactionRepository.getTransaction(userTransactionRequest.getUserToken(), userTransactionRequest.getCountTransaction());
        if (userTransactions.isEmpty()) {
            throw new TransactionNotFoundException("Transactions not found, please try again");
        }

        List<UserTransactionResponse> transactions = new ArrayList<>();

        for (UserTransactions trans : userTransactions) {
            int idBeer = trans.getIdBeer();
            int countBeer = trans.getCount();
            String date = trans.getDateTime().format(dateTimeFormatter);

            transactions.add(new UserTransactionResponse(idBeer, countBeer, date));
        }
        return transactions;
    }

    @Override
    public List<BeerResponse> getListBeers(BeerTransactionRequest beerRequest) throws SQLException, UserNotFoundException, BeerNotFoundException, JsonProcessingException {
        if (!userRepository.isUserExist(UUID.fromString(beerRequest.getUserToken()))) {
            throw new UserNotFoundException("User with this " + beerRequest.getUserToken() + " token not found. please register before by purchase");
        }

        transactionsValidator.forEach(x -> x.check(beerRequest));


        List<Beer> beerList = beerRepository.getBeerList(beerRequest.getCountBeer());
        if (beerList.isEmpty()) {
            throw new BeerNotFoundException("Beers not found, please try again");
        }

        Map<Integer, BeerContainer> beerContainerList = beerRepository.getBeerContainers();
        Map<Integer, BeerType> beerTypeList = beerRepository.getBeerTypes();


        List<BeerResponse> beerResponseList = new ArrayList<>();
        for (Beer beer : beerList) {
            BeerContainer beerContainer = beerContainerList.get(beer.getIdContainer());
            BeerType beerType = beerTypeList.get(beer.getIdTypeBeer());
            String countBeer = objectMapper.readTree(beer.getCountBeerJson()).get("count").asText();

            BeerResponse beerResponse = BeerResponse.builder().
                    name(beer.getName()).
                    container(beerContainer.getName()).
                    volume(beerContainer.getVolume()).
                    beerType(beerType.getName()).
                    alcoholContent(beer.getAlcoholContent()).
                    ibu(beer.getIbu()).
                    count(countBeer).
                    build();

            beerResponseList.add(beerResponse);
        }
        return beerResponseList;
    }

    private double getValueCountFromJson(String json) {
        try {
            return objectMapper.readTree(json).get("count").asDouble();
        } catch (JsonProcessingException e) {
            throw new WrongDataException("You enter Incorrect data");
        }
    }
}

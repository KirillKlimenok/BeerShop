package com.modsen.service;

import com.modsen.exception.BeerNotFoundException;
import com.modsen.exception.TooMuchValueCountTransactionException;
import com.modsen.exception.TooSmallValueCountTransactionException;
import com.modsen.exception.TransactionNotFoundException;
import com.modsen.exception.UserNotFoundException;
import com.modsen.repository.BeerRepository;
import com.modsen.repository.TransactionRepository;
import com.modsen.repository.UserRepository;
import com.modsen.repository.entytie.UserTransactions;
import com.modsen.service.dto.TransactionDto;
import com.modsen.сontroller.model.BeerRequest;
import com.modsen.сontroller.model.TransactionRequest;
import com.modsen.сontroller.model.TransactionResponse;
import lombok.AllArgsConstructor;

import java.sql.Date;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
public class UserActionServiceImpl implements UserActionService {
    private BeerRepository beerRepository;
    private UserRepository userRepository;
    private TransactionRepository transactionRepository;
    private List<Validator<BeerRequest>> beerValidators;
    private List<Validator<TransactionRequest>> transactionsValidator;
    private DateTimeFormatter dateTimeFormatter;

    @Override
    public void buyBeer(List<BeerRequest> listBeersRequest, String userToken) throws SQLException, UserNotFoundException, BeerNotFoundException {
        if (!userRepository.isUserExist(UUID.fromString(userToken))) {
            throw new UserNotFoundException("User with this " + userToken + " token not found. please register before by purchase");
        }

        listBeersRequest.forEach(beer -> beerValidators.forEach(validator -> validator.check(beer)));

        List<Integer> idBeerInBd = beerRepository.getAllBeers();

        if (idBeerInBd.isEmpty()) {
            throw new BeerNotFoundException("List beer is clear");
        }

        if (!listBeersRequest.stream().allMatch(x -> idBeerInBd.contains(x.getIdBeer()))) {
            throw new BeerNotFoundException("Please enter only existing positions");
        }

        Date date = new Date(System.currentTimeMillis());
        for (BeerRequest beerRequest : listBeersRequest) {
            int idBeer = beerRequest.getIdBeer();
            int countBeer = beerRequest.getCount();
            beerRepository.removeCountBeer(idBeer, countBeer);
            TransactionDto transaction = new TransactionDto(idBeer, countBeer, date);
            transactionRepository.save(transaction, userToken);
        }

    }

    @Override
    public List<TransactionResponse> getTransactions(TransactionRequest transactionRequest) throws SQLException, TransactionNotFoundException, UserNotFoundException {
        if (!userRepository.isUserExist(UUID.fromString(transactionRequest.getUserToken()))) {
            throw new UserNotFoundException("User with this " + transactionRequest.getUserToken() + " token not found. please register before by purchase");
        }

        try {
            transactionsValidator.forEach(x -> x.check(transactionRequest));
        } catch (TooMuchValueCountTransactionException e) {
            transactionRequest.setCount(20);
        } catch (TooSmallValueCountTransactionException e) {
            transactionRequest.setCount(5);
        }

        List<UserTransactions> userTransactions = transactionRepository.getTransaction(transactionRequest.getUserToken(), transactionRequest.getCount());
        if (userTransactions.isEmpty()) {
            throw new TransactionNotFoundException("Transactions not found, please try again");
        }

        List<TransactionResponse> transactions = new ArrayList<>();
        for (UserTransactions trans : userTransactions) {
            int idBeer = trans.getId_beer();
            int countBeer = trans.getCount();
            String date = trans.getDate_time().toLocalDate().format(dateTimeFormatter);

            transactions.add(new TransactionResponse(idBeer, countBeer, date));
        }
        return transactions;
    }
}

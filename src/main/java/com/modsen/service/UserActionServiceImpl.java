package com.modsen.service;

import com.modsen.exception.BeerNotFoundException;
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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@AllArgsConstructor
public class UserActionServiceImpl implements UserActionService {
    private BeerRepository beerRepository;
    private UserRepository userRepository;
    private TransactionRepository transactionRepository;
    private List<Validator<BeerRequest>> beerValidators;
    private DateTimeFormatter dateTimeFormatter;

    @Override
    public void buyBeer(List<BeerRequest> listBeersRequest, String userToken) throws SQLException, UserNotFoundException, BeerNotFoundException {
        listBeersRequest.forEach(beer -> beerValidators.forEach(validator -> validator.check(beer)));

        if (userRepository.isUserExist(UUID.fromString(userToken))) {
            throw new UserNotFoundException("User with this " + userToken + " token not found. please register before by purchase");
        }

        ResultSet resultSet = beerRepository.getAllBeers();

        if (resultSet.next()) {
            List<Integer> idBeerInBd = new ArrayList<>();
            while (resultSet.next()) {
                idBeerInBd.add(resultSet.getInt("id"));
            }
            //todo переписать(в случе если id запроса не нашёлся, выбрасывать ошибку с указанием проблемы)
            List<BeerRequest> trueBeerId = listBeersRequest.stream().filter(x -> idBeerInBd.contains(x.getIdBeer())).collect(Collectors.toList());
            Date date = new Date(System.currentTimeMillis());
            for (BeerRequest beerRequest : trueBeerId) {
                int idBeer = beerRequest.getIdBeer();
                int countBeer = beerRequest.getCount();
                beerRepository.removeCountBeer(idBeer, countBeer);
                TransactionDto transaction = new TransactionDto(idBeer, countBeer, date);
                transactionRepository.save(transaction, userToken);
            }
        } else {
            throw new BeerNotFoundException("List beer is clear");
        }

    }

    @Override
    public List<TransactionResponse> getTransactions(TransactionRequest transactionRequest) throws SQLException, TransactionNotFoundException, UserNotFoundException {
        if (transactionRequest.getCount() < 5) {
            transactionRequest.setCount(5);
        } else if (transactionRequest.getCount() > 20) {
            transactionRequest.setCount(20);
        }

        if (userRepository.isUserExist(UUID.fromString(transactionRequest.getUserToken()))) {
            List<UserTransactions> userTransactions = transactionRepository.getTransaction(transactionRequest.getUserToken(), transactionRequest.getCount());
            if (!userTransactions.isEmpty()) {
                List<TransactionResponse> transactions = new ArrayList<>();
                for (UserTransactions trans : userTransactions) {
                    int idBeer = (int) trans.getId_beer();
                    int countBeer = (int) trans.getCount();
                    String date = trans.getDate_time().toLocalDate().format(dateTimeFormatter);

                    transactions.add(new TransactionResponse(idBeer, countBeer, date));
                }
                return transactions;
            }
        } else {
            throw new UserNotFoundException("User with this " + transactionRequest.getUserToken() + " token not found. please register before by purchase");
        }
        throw new TransactionNotFoundException("Transactions not found, please try again");
    }
}
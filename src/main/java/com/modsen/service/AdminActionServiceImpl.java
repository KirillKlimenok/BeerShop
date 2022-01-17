package com.modsen.service;

import com.modsen.exception.AccessException;
import com.modsen.exception.BeerNotFoundException;
import com.modsen.exception.BeerParameterNotExistException;
import com.modsen.exception.DataAlreadyCreatedException;
import com.modsen.exception.TransactionNotFoundException;
import com.modsen.repository.AdminRepository;
import com.modsen.repository.BeerRepository;
import com.modsen.repository.TransactionRepository;
import com.modsen.repository.entytie.AdminTransaction;
import com.modsen.repository.entytie.Beer;
import com.modsen.repository.entytie.UserTransactions;
import com.modsen.service.dto.BeerDto;
import com.modsen.сontroller.model.AdminTransactionRequest;
import com.modsen.сontroller.model.AdminTransactionResponse;
import com.modsen.сontroller.model.BeerResponse;
import com.modsen.сontroller.model.UserTransactionResponse;
import lombok.AllArgsConstructor;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
public class AdminActionServiceImpl implements AdminActionService {
    private AdminRepository adminRepository;
    private BeerRepository beerRepository;
    private DateTimeFormatter dateTimeFormatter;
    private TransactionRepository transactionRepository;

    @Override
    public void addNewPosition(BeerResponse beerResponse) throws SQLException, BeerParameterNotExistException, DataAlreadyCreatedException {
        int beerTypeId = getBeerTypeIdIfExist(beerResponse);
        int beerContainerId = getBeerContainerIdIfExist(beerResponse);

        BeerDto beerDto = BeerDto.
                builder().
                name(beerResponse.getName()).
                idContainer(beerContainerId).
                idBeerType(beerTypeId).
                alcoholContent(beerResponse.getAlcoholContent()).
                ibu(beerResponse.getIbu()).
                countBeer(beerResponse.getCount()).
                build();

        if (beerRepository.isBeerExist(beerDto)) {
            throw new DataAlreadyCreatedException("Beer with this parameters already created");
        }

        LocalDateTime date = LocalDateTime.now();

        beerRepository.save(beerDto, date);
    }

    @Override
    public Beer changePosition(Beer beerRequest) throws SQLException, BeerNotFoundException {
        if (!beerRepository.isBeerExist(beerRequest.getId())) {
            throw new BeerNotFoundException("beer with this id: " + beerRequest.getId() + " not found");
        }

        Beer currentBeerDto = beerRepository.getBeerById(beerRequest.getId());

        currentBeerDto.setParam(beerRequest.getIdContainer(), beerRequest.getCount());

        LocalDateTime date = LocalDateTime.now();

        beerRepository.changeBeer(currentBeerDto, date);

        return currentBeerDto;
    }

    @Override
    public void checkIsAdmin(UUID token) throws SQLException, AccessException {
        if (!adminRepository.isUserAdmin(token)) {
            throw new AccessException("Access denied");
        }
    }

    @Override
    public List<AdminTransactionResponse> getTransactions(AdminTransactionRequest transactionRequest) throws SQLException {
        List<AdminTransaction> userTransactions = transactionRepository.getTransaction(transactionRequest.getCountTransactions());

        List<AdminTransactionResponse> transactions = new ArrayList<>();

        for (AdminTransaction trans : userTransactions) {
            String userId = trans.getUserId().toString();
            int idBeer = trans.getIdBeer();
            int countBeer = trans.getCount();
            String date = trans.getDateTime().format(dateTimeFormatter);

            transactions.add(new AdminTransactionResponse(userId, idBeer, countBeer, date));
        }

        return transactions;
    }

    private int getBeerContainerIdIfExist(BeerResponse beerResponse) throws SQLException, BeerParameterNotExistException {
        int beerContainerId;
        if (!beerRepository.isBeerContainerExist(beerResponse.getContainer(), beerResponse.getVolume())) {
            throw new BeerParameterNotExistException("Container Beer :" + beerResponse.getContainer() + " with volume: " + beerResponse.getVolume() + " not exits");
        } else {
            beerContainerId = beerRepository.getBeerContainerId(beerResponse.getContainer(), beerResponse.getVolume());
        }
        return beerContainerId;
    }

    private int getBeerTypeIdIfExist(BeerResponse beerResponse) throws SQLException, BeerParameterNotExistException {
        int beerTypeId;
        if (!beerRepository.isBeerTypeExist(beerResponse.getBeerType())) {
            throw new BeerParameterNotExistException("Beer type: " + beerResponse.getBeerType() + "not exist");
        } else {
            beerTypeId = beerRepository.getBeerTypeId(beerResponse.getBeerType());
        }
        return beerTypeId;
    }
}

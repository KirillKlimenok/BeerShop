package com.modsen.service;

import com.modsen.exception.AccessException;
import com.modsen.exception.BeerParameterNotExistException;
import com.modsen.repository.AdminRepository;
import com.modsen.repository.BeerRepository;
import com.modsen.service.dto.BeerDto;
import com.modsen.сontroller.model.BeerResponse;
import lombok.AllArgsConstructor;

import java.sql.SQLException;
import java.util.UUID;

@AllArgsConstructor
public class AdminActionServiceImpl implements AdminActionService {
    private AdminRepository adminRepository;
    private BeerRepository beerRepository;

    @Override
    public void addNewPosition(BeerResponse beerResponse) throws SQLException, BeerParameterNotExistException {
        int beerTypeId = getBeerTypeIdIfExist(beerResponse);
        int beerContainerId = getBeerContainerIdIfExist(beerResponse);

        BeerDto beerDto = BeerDto.
                builder().
                name(beerResponse.getName()).
                idContainer(beerContainerId).
                idBeerType(beerTypeId).
                alcoholContent(beerResponse.getAlcoholContent()).
                ibu(beerResponse.getIbu()).
                build();

        beerRepository.save(beerDto);
    }

    @Override
    public void checkIsAdmin(UUID token) throws SQLException, AccessException {
        if (!adminRepository.isUserAdmin(token)) {
            throw new AccessException("Access denied");
        }
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

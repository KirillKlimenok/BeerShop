package com.modsen.service;

import com.modsen.exception.AccessException;
import com.modsen.repository.AdminRepository;
import com.modsen.сontroller.model.BeerResponse;
import lombok.AllArgsConstructor;

import java.sql.SQLException;
import java.util.UUID;

@AllArgsConstructor
public class AdminActionServiceImpl implements AdminActionService {
    private AdminRepository adminRepository;

    @Override
    public void addNewPosition(BeerResponse beerResponse) {

    }

    @Override
    public void checkIsAdmin(UUID token) throws SQLException, AccessException {
        if(!adminRepository.isUserAdmin(token)){
            throw new AccessException("Access denied");
        }
    }
}

package com.modsen.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.modsen.exception.AccessException;
import com.modsen.exception.BeerNotFoundException;
import com.modsen.exception.TransactionException;
import com.modsen.exception.UserNotFoundException;
import com.modsen.exception.UserRegistrationException;
import com.modsen.repository.entytie.Beer;
import com.modsen.service.dto.BeerDto;
import com.modsen.сontroller.model.BeerResponse;
import lombok.AllArgsConstructor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
public class AddNewBeerPositionDoPostServiceImpl implements DoPostService {
    public static final String URL_REQUEST = "/beerShop/admin/buy-beer";

    private AdminActionService adminActionService;
    private ObjectMapper objectMapper;
    private String nameHeaderToken;
    private List<Validator<BeerResponse>> validators;

    @Override
    public void apply(HttpServletRequest request, HttpServletResponse response, String bodyRequest) throws IOException, SQLException, UserRegistrationException, UserNotFoundException, BeerNotFoundException, TransactionException, AccessException {
        String token = request.getHeader(nameHeaderToken);

        adminActionService.checkIsAdmin(UUID.fromString(token));

        BeerResponse beerResponse = objectMapper.readValue(bodyRequest, BeerResponse.class);

        validators.forEach(x -> x.check(beerResponse));

        adminActionService.addNewPosition();
    }

    @Override
    public String getUrl() {
        return URL_REQUEST;
    }
}

package com.modsen.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.modsen.exception.AccessException;
import com.modsen.exception.BeerNotFoundException;
import com.modsen.exception.BeerParameterNotExistException;
import com.modsen.repository.entytie.Beer;
import lombok.Builder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.UUID;

@Builder
public class ChangeBeerPositionDoPutService implements DoPutService {
    public static final String URL_REQUEST = "/beerShop/admin/beer/change-position";

    private AdminActionService adminActionService;
    private ObjectMapper objectMapper;
    private String nameHeaderToken;
    private Validator<Beer> countValidator;

    @Override
    public void apply(HttpServletRequest request, HttpServletResponse response, String bodyRequest) throws AccessException, SQLException, IOException, BeerNotFoundException, BeerParameterNotExistException {
        String token = request.getHeader(nameHeaderToken);

        adminActionService.checkIsAdmin(UUID.fromString(token));

        Beer beer = objectMapper.readValue(bodyRequest, Beer.class);

        if (beer.getCount() != null) {
            countValidator.check(beer);
        }

        Beer beerBd = adminActionService.changePosition(beer);

        response.setStatus(200);
        PrintWriter printWriter = response.getWriter();
        printWriter.write(objectMapper.writeValueAsString(beerBd));
        printWriter.close();
    }

    @Override
    public String getUrl() {
        return URL_REQUEST;
    }
}

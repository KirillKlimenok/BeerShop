package com.modsen.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.modsen.exception.AccessException;
import com.modsen.exception.BeerNotFoundException;
import com.modsen.exception.TransactionNotFoundException;
import com.modsen.exception.UserNotFoundException;
import com.modsen.сontroller.model.AdminTransactionRequest;
import com.modsen.сontroller.model.AdminTransactionResponse;
import com.modsen.сontroller.model.Transaction;
import lombok.Builder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

@Builder
public class GetAllUsersTransactionsDoGetService implements DoGetService {
    public static final String URL_REQUEST = "/beerShop/admin/get-users-transactions";
    private AdminActionService adminActionService;
    private ObjectMapper objectMapper;
    private String nameHeaderToken;
    private List<Validator<Transaction>> validator;

    @Override
    public void apply(HttpServletRequest request, HttpServletResponse response, String bodyRequest) throws IOException, UserNotFoundException, BeerNotFoundException, SQLException, TransactionNotFoundException, AccessException {
        String token = request.getHeader(nameHeaderToken);
        adminActionService.checkIsAdmin(UUID.fromString(token));

        AdminTransactionRequest transactionRequest = objectMapper.readValue(bodyRequest, AdminTransactionRequest.class);

        validator.forEach(x -> x.check(transactionRequest));

        List<AdminTransactionResponse> adminTransactionResponses = adminActionService.getTransactions(transactionRequest);
        String usersTransactionsJson = "";


        if (adminTransactionResponses.isEmpty()) {
            usersTransactionsJson = "Empty";
        } else {
            usersTransactionsJson = objectMapper.writeValueAsString(adminTransactionResponses);
        }

        response.setStatus(200);
        response.setContentType("application/json;Windows-1251;charset=utf-8");
        PrintWriter printWriter = response.getWriter();
        printWriter.println(usersTransactionsJson);
        printWriter.close();
    }

    @Override
    public String getUrl() {
        return URL_REQUEST;
    }
}

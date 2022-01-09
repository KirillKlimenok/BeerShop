package com.modsen.сontroller.model;

import lombok.Data;

import java.sql.Date;
import java.time.format.DateTimeFormatter;

@Data
public class TransactionResponse {
    int idBeer;
    int count;
    String date;

    public TransactionResponse(int idBeer, int count, String date) {
        this.idBeer = idBeer;
        this.count = count;
        this.date = date;
    }
}

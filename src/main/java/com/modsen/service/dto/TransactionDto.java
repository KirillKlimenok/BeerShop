package com.modsen.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Date;

@Data
@AllArgsConstructor
public class TransactionDto {
    private int idBeer;
    private int countBeer;
    private Date buyDate;
}

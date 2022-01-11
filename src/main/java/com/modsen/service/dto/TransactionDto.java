package com.modsen.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Date;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class TransactionDto {
    private int idBeer;
    private double countBeer;
    private LocalDateTime buyDate;
}

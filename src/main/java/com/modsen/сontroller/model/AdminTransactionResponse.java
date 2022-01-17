package com.modsen.сontroller.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminTransactionResponse {
    private String idUser;
    private int idBeer;
    private float countBeer;
    private String localDateTime;
}

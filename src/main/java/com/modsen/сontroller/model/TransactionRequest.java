package com.modsen.сontroller.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TransactionRequest {
    private String userToken;
    private int count;

    public TransactionRequest(int count) {
        this.count = count;
    }
}

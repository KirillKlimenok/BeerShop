package com.modsen.сontroller.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
public class BeerTransactionRequest implements Transaction {
    private String userToken;
    @NonNull
    private Integer countBeer;

    @Override
    public void setCountTransaction(int value) {
        this.countBeer = value;
    }

    @Override
    public int getCountTransaction() {
        return countBeer;
    }
}

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
public class UserTransactionRequest implements Transaction {
    private String userToken;
    @NonNull
    private Integer countTransaction;

    @Override
    public void setCountTransaction(int value) {
        this.countTransaction = value;
    }

    @Override
    public int getCountTransaction() {
        return countTransaction;
    }
}

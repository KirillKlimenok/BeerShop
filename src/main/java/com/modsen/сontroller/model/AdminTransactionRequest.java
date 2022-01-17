package com.modsen.сontroller.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminTransactionRequest implements Transaction {
    private int countTransactions;

    @Override
    public void setCountTransaction(int value) {
        this.countTransactions = value;
    }

    @Override
    public int getCountTransaction() {
        return countTransactions;
    }
}

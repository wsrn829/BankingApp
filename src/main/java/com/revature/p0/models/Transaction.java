package com.revature.p0.models;

import com.revature.p0.models.Type;

public class Transaction {
    private int accountId;
    private Double amount;
    private Type type;

    public Transaction () {
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}
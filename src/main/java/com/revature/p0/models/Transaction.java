package com.revature.p0.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transaction {
    private int accountId;
    private BigDecimal amount;
    private LocalDateTime timeStamp;
    private String type;

    public Transaction () {

    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(LocalDateTime timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
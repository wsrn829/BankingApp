package com.revature.p0.models;

import java.time.LocalDateTime;

public class Transaction {
    private Integer transactionId;
    private Integer accountId;
    private String type;
    private double amount;
    private double balanceAfterTransaction;
    private LocalDateTime timestamp;

    public Transaction(Integer transactionId, Integer accountId, String type, double amount, double balanceAfterTransaction, LocalDateTime timestamp) {
        this.transactionId = transactionId;
        this.accountId = accountId;
        this.type = type;
        this.amount = amount;
        this.balanceAfterTransaction = balanceAfterTransaction;
        this.timestamp = timestamp;
    }

    public Transaction(Integer accountId, String type, double amount, double balanceAfterTransaction) {
        this(null, accountId, type, amount, balanceAfterTransaction, LocalDateTime.now());
    }

    public Integer getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Integer transactionId) {
        this.transactionId = transactionId;
    }

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getBalanceAfterTransaction() {
        return balanceAfterTransaction;
    }

    public void setBalanceAfterTransaction(double balanceAfterTransaction) {
        this.balanceAfterTransaction = balanceAfterTransaction;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "transactionId=" + transactionId +
                ", accountId=" + accountId +
                ", type='" + type + '\'' +
                ", amount=" + amount +
                ", balanceAfterTransaction=" + balanceAfterTransaction +
                ", timestamp=" + timestamp +
                '}';
    }
}

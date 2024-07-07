package com.revature.p0.models;

import java.util.List;

public class Account {
    private int accountId;
    private String accountNumber;
    private Double balance;
    private List<Transaction> transactions;

    public Account () {

    }

    public Account (String accountNumber, Double balance, List<Transaction> transactions) {
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.transactions = transactions;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public Double getBalance () {
        return balance;
    }

    public void setBalance (Double balance) {
        this.balance = balance;
    }

    public List<Transaction> getTransactions () {
        return transactions;
    }

    public void setTransactions (List<Transaction> transactions) {
        this.transactions = transactions;
    }
}

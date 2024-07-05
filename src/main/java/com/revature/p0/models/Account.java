package com.revature.p0.models;

import java.math.BigDecimal;
import java.util.List;

public class Account {
    private int userId;
    private String accountNumber;
    private BigDecimal balance;
    private List<Transaction> transactions;

    public Account () {

    }

    public Account (String accountNumber, BigDecimal balance, List<Transaction> transactions) {
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

    public BigDecimal getBalance () {
        return balance;
    }

    public void setBalance (BigDecimal balance) {
        this.balance = balance;
    }

    public List<Transaction> getTransactions () {
        return transactions;
    }

    public void setTransactions (List<Transaction> transactions) {
        this.transactions = transactions;
    }
}

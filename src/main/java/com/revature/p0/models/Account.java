package com.revature.p0.models;

import java.util.ArrayList;
import java.util.List;

public class Account {
    private Integer accountId;
    private Integer userId;
    private String accountNumber;
    private Double balance;
    private List<Transaction> transactions;

    public Account(Integer accountId, String accountNumber, Integer userId, Double balance) {
        this.accountId = accountId;
        this.accountNumber = accountNumber;
        this.userId = userId;
        this.balance = balance;
        this.transactions = new ArrayList<>();
    }

    public Account() {
    }

    // Getters and setters
    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    // Other methods
    public void addTransaction(Transaction transaction) {
        this.transactions.add(transaction);
    }

    public void deposit(double amount) {
        if (amount > 0) {
            this.balance += amount;
            this.addTransaction(new Transaction(this.accountId, "deposit", amount, this.balance));
        }
    }

    public void withdraw(double amount) {
        if (amount > 0 && this.balance >= amount) {
            this.balance -= amount;
            this.addTransaction(new Transaction(this.accountId, "withdrawal", amount, this.balance));
        }
    }

    public void transferTo(Account destinationAccount, double amount) {
        if (amount > 0 && this.balance >= amount) {
            this.withdraw(amount);
            destinationAccount.deposit(amount);
            this.addTransaction(new Transaction(this.accountId, "transfer_out", amount, this.balance));
            destinationAccount.addTransaction(new Transaction(destinationAccount.getAccountId(), "transfer_in", amount, destinationAccount.getBalance()));
        }
    }
}

package com.revature.p0.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Account {
    private Integer accountId;
    private String accountNumber;
    private BigDecimal balance;
    private Integer userId;

    @JsonManagedReference
    private List<Transaction> transactions = new ArrayList<>();
    private Object LocalDateTime;

    public Account(Integer accountId, String accountNumber, Integer userId, BigDecimal balance) {
        this.accountId = accountId;
        this.accountNumber = accountNumber;
        this.userId = userId;
        this.balance = balance;
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

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }


    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public void addTransaction(Transaction transaction) {
        if (this.transactions == null) {
            this.transactions = new ArrayList<>();
        }
        this.transactions.add(transaction);
    }


    public void deposit(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) > 0) {
            this.balance = this.balance.add(amount);
            Transaction depositTransaction = new Transaction(this.accountId, "deposit", amount, this);
            this.addTransaction(depositTransaction);
        }
    }

    public void withdraw(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) > 0 && this.balance.compareTo(amount) >= 0) {
            this.balance = this.balance.subtract(amount);
            Transaction withdrawalTransaction = new Transaction(this.accountId, "withdrawal", amount, this);
            this.addTransaction(withdrawalTransaction);
        }
    }

    public void transferTo(Account destinationAccount, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) > 0 && this.balance.compareTo(amount) >= 0) {
            this.withdraw(amount);
            destinationAccount.deposit(amount);
            this.addTransaction(new Transaction(this.accountId, "transfer_out", amount, this.balance));
            destinationAccount.addTransaction(new Transaction(destinationAccount.getAccountId(), "transfer_in", amount, destinationAccount.getBalance()));
        }
    }
}

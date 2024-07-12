package com.revature.p0.models;
import com.fasterxml.jackson.annotation.JsonBackReference;
import java.math.BigDecimal;

public class Transaction {
    private Integer transactionId;
    private Integer accountId;
    private String type;
    private BigDecimal amount;
    @JsonBackReference
    private Account account;

    public Transaction(Integer transactionId, Integer accountId, String type, BigDecimal amount, Account account) {
        this.transactionId = transactionId;
        this.accountId = accountId;
        this.type = type;
        this.amount = amount;
        this.account = account;
    }

    public Transaction(Integer transactionId, Integer accountId, String type, BigDecimal amount) {
        this.transactionId = transactionId;
        this.accountId = accountId;
        this.type = type;
        this.amount = amount;
    }

    public Transaction(Integer accountId, String type, BigDecimal amount, Account account) {
        this(null, accountId, type, amount);
    }

    public Transaction(Integer accountId, String type, BigDecimal amount) {
        this.accountId = accountId;
        this.type = type;
        this.amount = amount;
    }

    public Transaction(Integer accountId, String transferIn, BigDecimal amount, BigDecimal balance) {
        this.accountId = accountId;
        this.type = transferIn;
        this.amount = amount;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
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

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
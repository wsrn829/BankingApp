package com.revature.p0.services;

import com.revature.p0.daos.TransactionDao;
import com.revature.p0.models.Transaction;

import java.sql.SQLException;
import java.util.List;

public class TransactionService {
    private final TransactionDao transactionDao;

    public TransactionService(TransactionDao transactionDao) {
        this.transactionDao = transactionDao;
    }

    public void addTransaction(Transaction transaction) throws SQLException {
        transactionDao.createTransaction(transaction);
    }

    public Transaction getTransactionById(int transactionId) throws SQLException {
        return transactionDao.getTransactionById(transactionId);
    }

    public List<Transaction> getTransactionsByAccountId(int accountId) throws SQLException {
        return transactionDao.getTransactionsByAccountId(accountId);
    }
}

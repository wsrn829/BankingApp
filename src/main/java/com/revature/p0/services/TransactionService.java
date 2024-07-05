package com.revature.p0.services;

import com.revature.p0.daos.TransactionDao;

public class TransactionService {
    TransactionDao transactionDao;

    public TransactionService(TransactionDao transactionDao) {
        this.transactionDao = transactionDao;
    }
}

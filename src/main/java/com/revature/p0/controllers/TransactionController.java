package com.revature.p0.controllers;

import com.revature.p0.services.TransactionService;

public class TransactionController {
    TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }
}
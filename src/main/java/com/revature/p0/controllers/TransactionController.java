package com.revature.p0.controllers;

import com.revature.p0.models.Transaction;
import com.revature.p0.services.TransactionService;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.sql.SQLException;
import java.util.List;

public class TransactionController {
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    public void registerEndpoints(Javalin app) {
        app.get("/transactions/{transactionId}", this::getTransactionById);
        app.get("/accounts/{accountId}/transactions", this::getTransactionsByAccountId);
    }

    private void getTransactionById(Context ctx) throws SQLException {
        int transactionId = Integer.parseInt(ctx.pathParam("transactionId"));
        Transaction transaction = transactionService.getTransactionById(transactionId);
        if (transaction != null) {
            ctx.json(transaction);
        } else {
            ctx.status(404).result("Transaction not found");
        }
    }

    private void getTransactionsByAccountId(Context ctx) throws SQLException {
        int accountId = Integer.parseInt(ctx.pathParam("accountId"));
        List<Transaction> transactions = transactionService.getTransactionsByAccountId(accountId);
        if (!transactions.isEmpty()) {
            ctx.json(transactions);
        } else {
            ctx.status(404).result("No transactions found for this account");
        }
    }
}

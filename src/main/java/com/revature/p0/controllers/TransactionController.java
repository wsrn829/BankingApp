package com.revature.p0.controllers;

import com.revature.p0.models.Transaction;
import com.revature.p0.models.TransferRequest;
import com.revature.p0.services.TransactionService;
import io.javalin.Javalin;
import io.javalin.http.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.math.BigDecimal;
import java.util.Objects;

public class TransactionController {
    private final TransactionService transactionService;
    private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    public void registerEndpoints(Javalin app) {
        app.get("/transactions/{transactionId}", this::getTransactionById);
        app.get("/accounts/{accountId}/transactions", this::getTransactionsByAccountId);
        app.post("/accounts/transfer", this::transferBetweenAccounts);
    }


    private void transferBetweenAccounts(Context ctx) {
        try {
            TransferRequest transferRequest = ctx.bodyAsClass(TransferRequest.class);
            if (transferRequest == null || transferRequest.getSourceAccountId() == null || transferRequest.getDestinationAccountId() == null || transferRequest.getAmount() == null) {
                ctx.status(400).result("Request body is missing or incomplete");
                return;
            }

            int sourceAccountId = transferRequest.getSourceAccountId();
            int destinationAccountId = transferRequest.getDestinationAccountId();
            BigDecimal amount = transferRequest.getAmount();

            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                ctx.status(400).result("Amount must be greater than zero");
                return;
            }

            Connection conn = null;
            transactionService.transferBetweenAccounts(conn, sourceAccountId, destinationAccountId, amount);
            ctx.status(200).result("Transfer successful");
        } catch (NumberFormatException e) {
            logger.error("Error parsing parameters for transfer", e);
            ctx.status(400).result("Invalid parameter format");
        } catch (SQLException e) {
            logger.error("SQL exception in transferBetweenAccounts", e);
            ctx.status(500).result("Internal server error");
        } catch (Exception e) {
            logger.error("Unexpected error during transfer", e);
            ctx.status(500).result("Internal server error");
        }
    }


    private void getTransactionById(Context ctx) {
        try {
            int transactionId = Integer.parseInt(ctx.pathParam("transactionId"));
            if (transactionId <= 0) {
                ctx.status(400).result("Invalid transaction ID");
                return;
            }
            Transaction transaction = transactionService.getTransactionById(transactionId);
            if (transaction != null) {
                ctx.json(transaction);
            } else {
                ctx.status(404).result("Transaction not found");
            }
        } catch (NumberFormatException e) {
            logger.error("Error parsing transaction ID", e);
            ctx.status(400).result("Invalid transaction ID format");
        } catch (SQLException e) {
            logger.error("SQL exception in getTransactionById", e);
            ctx.status(500).result("Internal server error");
        }
    }

    private void getTransactionsByAccountId(Context ctx) {
        try {
            int accountId = Integer.parseInt(ctx.pathParam("accountId"));
            if (accountId <= 0) {
                ctx.status(400).result("Invalid account ID");
                return;
            }
            List<Transaction> transactions = transactionService.getTransactionsByAccountId(accountId);
            if (!transactions.isEmpty()) {
                ctx.json(transactions);
            } else {
                ctx.status(404).result("No transactions found for this account");
            }
        } catch (NumberFormatException e) {
            logger.error("Error parsing account ID", e);
            ctx.status(400).result("Invalid account ID format");
        } catch (SQLException e) {
            logger.error("SQL exception in getTransactionsByAccountId", e);
            ctx.status(500).result("Internal server error");
        }
    }
}
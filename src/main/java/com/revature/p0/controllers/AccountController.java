package com.revature.p0.controllers;

import com.revature.p0.daos.AccountDao;
import com.revature.p0.daos.TransactionDao;
import com.revature.p0.dto.AmountWrapper;
import com.revature.p0.exceptions.AccountNotFoundException;
import com.revature.p0.exceptions.InsufficientFundsException;
import com.revature.p0.models.Account;
import com.revature.p0.models.Transaction;
import com.revature.p0.services.AccountService;
import com.revature.p0.utils.ConnectionUtil;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;
import java.math.BigDecimal;

public class AccountController {
    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    public void registerEndpoints(Javalin app) {
        app.post("/accounts", this::createAccount);
        app.get("/accounts/{accountId}", this::getAccountById);
        app.get("/users/{userId}/accounts", this::getAccountsByUserId);
        app.put("/accounts/{accountId}/deposit", this::deposit);
        app.put("/accounts/{accountId}/withdraw", this::withdraw);
        app.put("/accounts/{sourceAccountId}/transfer/{destinationAccountId}", this::transfer);
        app.delete("/accounts/{accountId}", this::deleteAccount);
    }

    private void createAccount(Context ctx) {
        try {
            Account account = ctx.bodyAsClass(Account.class);
            accountService.createAccount(account);
            ctx.status(201).json(account);
        } catch (SQLException e) {
            ctx.status(500).result("Failed to create account");
        }
    }

    private void getAccountById(Context ctx) {
        try {
            int accountId = Integer.parseInt(ctx.pathParam("accountId"));
            Account account = accountService.getAccountById(accountId);
            if (account != null) {
                ctx.json(account);
            } else {
                ctx.status(404).result("Account not found");
            }
        } catch (NumberFormatException e) {
            ctx.status(400).result("Invalid account ID format");
        } catch (SQLException e) {
            ctx.status(500).result("Database error");
        }
    }

    private void getAccountsByUserId(Context ctx) {
        try {
            int userId = Integer.parseInt(ctx.pathParam("userId"));
            ctx.json(accountService.getAccountsByUserId(userId));
        } catch (NumberFormatException e) {
            ctx.status(400).result("Invalid user ID format");
        } catch (SQLException e) {
            ctx.status(500).result("Database error");
        }
    }

    private void deposit(Context ctx) {
        try {
            int accountId = Integer.parseInt(ctx.pathParam("accountId"));
            AmountWrapper amountWrapper = ctx.bodyAsClass(AmountWrapper.class);
            BigDecimal amount = amountWrapper.getAmount();

            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                ctx.status(400).result("Amount must be positive");
                return;
            }

            Account account = accountService.getAccountById(accountId);
            if (account == null) {
                ctx.status(404).result("Account not found");
                return;
            }

            // Ensure accountService.deposit method manages database connection properly
            accountService.deposit(account, amount);
            ctx.status(200).json(account);
        } catch (NumberFormatException e) {
            ctx.status(400).result("Invalid account ID or amount format");
        } catch (SQLException e) {
            // Improved error handling for database-related exceptions
            ctx.status(500).result("Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void withdraw(Context ctx) {
        try {
            int accountId = Integer.parseInt(ctx.pathParam("accountId"));
            AmountWrapper amountWrapper = ctx.bodyAsClass(AmountWrapper.class); // Parse the amount from the request body
            BigDecimal amount = amountWrapper.getAmount(); // Get the BigDecimal amount

            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                ctx.status(400).result("Amount must be greater than 0");
                return;
            }

            Account account = accountService.getAccountById(accountId); // Retrieve the Account object
            if (account == null) {
                ctx.status(404).result("Account not found");
                return;
            }

            accountService.withdraw(account, amount); // Perform the withdrawal
            ctx.status(200).result("Withdrawal successful");
        } catch (NumberFormatException e) {
            ctx.status(400).result("Invalid account ID format");
        } catch (SQLException e) {
            ctx.status(500).result("Database error: " + e.getMessage());
        }
    }

    private void transfer(Context ctx) {
        try (Connection conn = ConnectionUtil.getConnection()) {
            conn.setAutoCommit(false); // Start transaction
            AmountWrapper amountWrapper = ctx.bodyAsClass(AmountWrapper.class);
            int sourceAccountId = amountWrapper.getSourceAccountId();
            int destinationAccountId = amountWrapper.getDestinationAccountId();
            BigDecimal amount = amountWrapper.getAmount();

            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                ctx.status(400).result("Amount must be positive");
                return;
            }

            accountService.transferBetweenAccounts(conn, sourceAccountId, destinationAccountId, amount);
            ctx.status(200).result("Transfer successful");
        } catch (NumberFormatException e) {
            ctx.status(400).result("Invalid account ID or amount format");
        } catch (NullPointerException e) {
            ctx.status(400).result("Missing required JSON parameters");
        } catch (SQLException e) {
            ctx.status(500).result("Database error during transfer: " + e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InsufficientFundsException e) {
            throw new RuntimeException(e);
        } catch (AccountNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void deleteAccount(Context ctx) {
        try {
            int accountId = Integer.parseInt(ctx.pathParam("accountId"));
            accountService.deleteAccount(accountId);
            ctx.status(200).json("{ \"message\": \"Account with ID " + accountId + " successfully deleted.\" }");
        } catch (NumberFormatException e) {
            ctx.status(400).result("Invalid account ID format");
        } catch (SQLException e) {
            ctx.status(500).result("Database error during account deletion");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

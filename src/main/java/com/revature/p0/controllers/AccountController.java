package com.revature.p0.controllers;

import com.revature.p0.models.Account;
import com.revature.p0.services.AccountService;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.sql.SQLException;
import java.util.Objects;

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

    private void createAccount(Context ctx) throws SQLException {
        Account account = ctx.bodyAsClass(Account.class);
        accountService.createAccount(account);
        ctx.status(201).json(account);
    }

    private void getAccountById(Context ctx) throws SQLException {
        int accountId = Integer.parseInt(ctx.pathParam("accountId"));
        Account account = accountService.getAccountById(accountId);
        if (account != null) {
            ctx.json(account);
        } else {
            ctx.status(404).result("Account not found");
        }
    }

    private void getAccountsByUserId(Context ctx) throws SQLException {
        int userId = Integer.parseInt(ctx.pathParam("userId"));
        ctx.json(accountService.getAccountsByUserId(userId));
    }

    private void deposit(Context ctx) throws SQLException {
        int accountId = Integer.parseInt(ctx.pathParam("accountId"));
        double amount = Double.parseDouble(Objects.requireNonNull(ctx.formParam("amount")));
        Account account = accountService.getAccountById(accountId);
        if (account != null) {
            accountService.deposit(account, amount);
            ctx.status(200).json(account);
        } else {
            ctx.status(404).result("Account not found");
        }
    }

    private void withdraw(Context ctx) throws SQLException {
        int accountId = Integer.parseInt(ctx.pathParam("accountId"));
        double amount = Double.parseDouble(Objects.requireNonNull(ctx.formParam("amount")));
        Account account = accountService.getAccountById(accountId);
        if (account != null) {
            accountService.withdraw(account, amount);
            ctx.status(200).json(account);
        } else {
            ctx.status(404).result("Account not found");
        }
    }

    private void transfer(Context ctx) throws SQLException {
        int sourceAccountId = Integer.parseInt(ctx.pathParam("sourceAccountId"));
        int destinationAccountId = Integer.parseInt(ctx.pathParam("destinationAccountId"));
        double amount = Double.parseDouble(Objects.requireNonNull(ctx.formParam("amount")));
        accountService.transferBetweenAccounts(sourceAccountId, destinationAccountId, amount);
        ctx.status(200).result("Transfer successful");
    }

    private void deleteAccount(Context ctx) throws SQLException {
        int accountId = Integer.parseInt(ctx.pathParam("accountId"));
        accountService.deleteAccount(accountId);
        ctx.status(204);
    }
}

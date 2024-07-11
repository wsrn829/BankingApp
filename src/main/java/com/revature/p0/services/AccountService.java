package com.revature.p0.services;

import com.revature.p0.daos.AccountDao;
import com.revature.p0.daos.TransactionDao;
import com.revature.p0.models.Account;
import com.revature.p0.models.Transaction;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AccountService {
    private AccountDao accountDao;
    private TransactionDao transactionDao;
    private Connection connection;
    private static final Logger logger = LoggerFactory.getLogger(AccountService.class);

    public AccountService(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    public AccountService(Connection connection) {
        this.connection = connection;
        this.accountDao = new AccountDao(connection);
    }

    public AccountService(AccountDao accountDao, TransactionDao transactionDao, Connection connection) {
        this.accountDao = accountDao;
        this.transactionDao = transactionDao;
        this.connection = connection;
    }

    public AccountService(AccountDao accountDao, TransactionDao transactionDao) {
        this.accountDao = accountDao;
        this.transactionDao = transactionDao;
    }

    public void createAccount(Account account) throws SQLException {
        try {
            if (this.accountDao != null) {
                this.accountDao.createAccount(account);
            } else {
                throw new IllegalStateException("AccountDao is null in AccountService");
            }
        } catch (SQLException e) {
            logger.error("Failed to create account: {}", e.getMessage());
            logger.error("Stack trace:", e);
            throw e; // Re-throw the exception after logging
        }
    }

    public Account getAccountById(int accountId) throws SQLException {
        return accountDao.getAccountById(accountId);
    }

    public List<Account> getAccountsByUserId(int userId) throws SQLException {
        return accountDao.getAccountsByUserId(userId);
    }

    public void updateAccountBalance(Account account) throws SQLException {
        accountDao.updateAccountBalance(account);
    }

    public void deleteAccount(int accountId) throws SQLException {
        accountDao.deleteAccount(accountId);
    }

    public void deposit(Account account, double amount) throws SQLException {
        account.deposit(amount);
        accountDao.updateAccountBalance(account);
        Transaction transaction = new Transaction(null, account.getAccountId(), "deposit", amount, account.getBalance(), null);
        transactionDao.createTransaction(transaction);
    }

    public void withdraw(Account account, double amount) throws SQLException {
        account.withdraw(amount);
        accountDao.updateAccountBalance(account);
        Transaction transaction = new Transaction(null, account.getAccountId(), "withdrawal", amount, account.getBalance(), null);
        transactionDao.createTransaction(transaction);
    }

    public void transferBetweenAccounts(int sourceAccountId, int destinationAccountId, double amount) throws SQLException {
        accountDao.transferBetweenAccounts(sourceAccountId, destinationAccountId, amount);
    }
}

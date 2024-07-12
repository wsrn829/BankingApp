package com.revature.p0.services;

import com.revature.p0.daos.AccountDao;
import com.revature.p0.daos.TransactionDao;
import com.revature.p0.exceptions.AccountNotFoundException;
import com.revature.p0.exceptions.InsufficientFundsException;
import com.revature.p0.models.Account;
import com.revature.p0.models.Transaction;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

import com.revature.p0.utils.ConnectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AccountService {
    private final AccountDao accountDao;
    private final TransactionDao transactionDao;
    private final Connection connection;
    private static final Logger logger = LoggerFactory.getLogger(AccountService.class);

    public AccountService(AccountDao accountDao, TransactionDao transactionDao, Connection connection) {
        this.accountDao = Objects.requireNonNull(accountDao, "accountDao cannot be null");
        this.transactionDao = Objects.requireNonNull(transactionDao, "transactionDao cannot be null");
        this.connection = Objects.requireNonNull(connection, "connection cannot be null");
    }

    public void createAccount(Account account) throws SQLException {
        try {
            accountDao.createAccount(account);
        } catch (SQLException e) {
            logger.error("Failed to create account: {}", e.getMessage(), e);
            throw e;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Account getAccountById(int accountId) throws SQLException {
        return accountDao.getAccountById(accountId);
    }

    public List<Account> getAccountsByUserId(int userId) throws SQLException {
        return accountDao.getAccountsByUserId(userId);
    }

    public void updateAccountBalance(Account account) throws SQLException {
        accountDao.updateAccountBalance(account, connection);
    }

    public void deleteAccount(int accountId) throws SQLException, IOException {
        accountDao.deleteAccount(accountId);
    }

    public void deposit(Account account, BigDecimal amount) throws SQLException {
        account.deposit(amount);
        accountDao.updateAccountBalance(account, connection);
        Transaction transaction = new Transaction(account.getAccountId(), "deposit", amount, account.getBalance());
        transactionDao.createTransaction(transaction, connection);
    }

    public void withdraw(Account account, BigDecimal amount) throws SQLException {
        account.withdraw(amount);
        accountDao.updateAccountBalance(account, connection);
        Transaction transaction = new Transaction(account.getAccountId(), "withdrawal", amount, account.getBalance());
        transactionDao.createTransaction(transaction, connection);
    }

    public void transferBetweenAccounts(Connection conn, int sourceAccountId, int destinationAccountId, BigDecimal amount) throws SQLException, InsufficientFundsException, AccountNotFoundException {
        try {
            conn.setAutoCommit(false); // Start transaction

            // Validate accounts and balance before proceeding
            if (!transactionDao.accountExists(sourceAccountId, conn) || !transactionDao.accountExists(destinationAccountId, conn)) {
                throw new AccountNotFoundException("One or both accounts not found.");
            }
            if (transactionDao.getCurrentBalance(sourceAccountId, conn).compareTo(amount) < 0) {
                throw new InsufficientFundsException("Source account has insufficient funds.");
            }

            // Withdraw from the source account
            transactionDao.withdraw(sourceAccountId, amount, conn);
            // Deposit to the destination account
            transactionDao.deposit(destinationAccountId, amount, conn);

            conn.commit(); // Commit transaction
        } catch (SQLException | InsufficientFundsException | AccountNotFoundException e) {
            if (conn != null) {
                conn.rollback(); // Rollback transaction on error
            }
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true); // Restore auto-commit mode
                conn.close(); // Close connection
            }
        }
    }
}

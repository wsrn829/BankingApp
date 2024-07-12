package com.revature.p0.services;

import com.revature.p0.daos.TransactionDao;
import com.revature.p0.exceptions.AccountNotFoundException;
import com.revature.p0.exceptions.InsufficientFundsException;
import com.revature.p0.models.Transaction;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class TransactionService {
    private final TransactionDao transactionDao;

    public TransactionService(TransactionDao transactionDao) {
        this.transactionDao = transactionDao;
    }

    public void addTransaction(Transaction transaction) throws SQLException {
        Connection connection = null;
        transactionDao.createTransaction(transaction, connection);
    }

    public Transaction getTransactionById(int transactionId) throws SQLException {
        return transactionDao.getTransactionById(transactionId);
    }

    public List<Transaction> getTransactionsByAccountId(int accountId) throws SQLException {
        return transactionDao.getTransactionsByAccountId(accountId);
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
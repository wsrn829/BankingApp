package com.revature.p0.daos;

import com.revature.p0.models.Transaction;
import com.revature.p0.utils.ConnectionUtil;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

public class TransactionDao {
    private final Connection connection;

    public TransactionDao(Connection connection) {
        this.connection = connection;
    }

    // Create a new transaction
    public void createTransaction(Transaction transaction, Connection connection) {
        String sql = "INSERT INTO transactions (account_id, type, amount) VALUES (?, ?, ?)";
        try (Connection conn = ConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, transaction.getAccountId());
            pstmt.setString(2, transaction.getType());
            pstmt.setBigDecimal(3, transaction.getAmount());
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating transaction failed, no rows affected.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    transaction.setTransactionId((int) generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Creating transaction failed, no ID obtained.");
                }
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    // Read transaction by transaction ID
    public Transaction getTransactionById(int transactionId) throws SQLException {
        String sql = "SELECT * FROM transactions WHERE transaction_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, transactionId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return new Transaction(
                            resultSet.getInt("transaction_id"),
                            resultSet.getInt("account_id"),
                            resultSet.getString("type"),
                            resultSet.getBigDecimal("amount")
                    );
                }
            }
        }
        return null;
    }

    // Read transactions by account ID
    public List<Transaction> getTransactionsByAccountId(int accountId) throws SQLException {
        String sql = "SELECT * FROM transactions WHERE account_id = ?";
        List<Transaction> transactions = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, accountId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Transaction transaction = new Transaction(
                            resultSet.getInt("transaction_id"),
                            resultSet.getInt("account_id"),
                            resultSet.getString("type"),
                            resultSet.getBigDecimal("amount")
                    );
                    transactions.add(transaction);
                }
            }
        }
        return transactions;
    }

    // Method to deposit amount into an account
    public void deposit(int accountId, BigDecimal amount, Connection connection) throws SQLException {
        BigDecimal currentBalance = getCurrentBalance(accountId, connection);
        BigDecimal newBalance = currentBalance.add(amount);
        updateAccountBalance(accountId, newBalance);

        Transaction depositTransaction = new Transaction(accountId, "deposit", amount, newBalance);
        createTransaction(depositTransaction, this.connection);
    }

    // Method to withdraw amount from an account
    public void withdraw(int accountId, BigDecimal amount, Connection connection) throws SQLException {
        BigDecimal currentBalance = getCurrentBalance(accountId, connection);
        if (currentBalance.compareTo(amount) >= 0) { // Ensure sufficient funds
            BigDecimal newBalance = currentBalance.subtract(amount);
            updateAccountBalance(accountId, newBalance);

            Transaction withdrawalTransaction = new Transaction(accountId, "withdrawal", amount, newBalance);
            createTransaction(withdrawalTransaction, this.connection);
        } else {
            throw new SQLException("Insufficient funds for withdrawal.");
        }
    }

    // Helper method to update account balance
    private void updateAccountBalance(int accountId, BigDecimal newBalance) throws SQLException {
        String sql = "UPDATE accounts SET balance = ? WHERE account_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setBigDecimal(1, newBalance);
            preparedStatement.setInt(2, accountId);
            preparedStatement.executeUpdate();
        }
    }

    public BigDecimal getCurrentBalance(int accountId, Connection connection) throws SQLException {
        String sql = "SELECT balance FROM accounts WHERE account_id = ?";
        try (PreparedStatement preparedStatement = this.connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, accountId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getBigDecimal("balance");
                } else {
                    throw new SQLException("Account not found with ID: " + accountId);
                }
            }
        }
    }

    public void beginTransaction() throws SQLException {
        ensureConnectionIsOpen();
        this.connection.setAutoCommit(false);
    }

    public void commitTransaction() throws SQLException {
        ensureConnectionIsOpen();
        this.connection.commit();
        this.connection.setAutoCommit(true);
    }

    public void rollbackTransaction() throws SQLException {
        ensureConnectionIsOpen();
        this.connection.rollback();
        this.connection.setAutoCommit(true);
    }

    // Existing methods remain unchanged

    private void ensureConnectionIsOpen() throws SQLException {
        if (this.connection == null || this.connection.isClosed()) {
            throw new SQLException("Operation cannot proceed on a closed connection.");
        }
    }

    public boolean accountDoesNotExist(int accountId) throws SQLException {
        return !accountExists(accountId, connection);
    }

    public boolean accountExists(int accountId, Connection connection) throws SQLException {
        String sql = "SELECT COUNT(*) FROM accounts WHERE account_id = ?";
        try (PreparedStatement preparedStatement = this.connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, accountId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        }
        return false;
    }
}
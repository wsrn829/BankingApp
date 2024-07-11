package com.revature.p0.daos;

import com.revature.p0.models.Transaction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TransactionDao {
    Connection connection;

    public TransactionDao(Connection connection) {
        this.connection = connection;
    }

    // Create a new transaction
    public void createTransaction(Transaction transaction) throws SQLException {
        String sql = "INSERT INTO transactions (account_id, type, amount, balance_after_transaction, timestamp) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setInt(1, transaction.getAccountId());
            preparedStatement.setString(2, transaction.getType());
            preparedStatement.setDouble(3, transaction.getAmount());
            preparedStatement.setDouble(4, transaction.getBalanceAfterTransaction());
            preparedStatement.setTimestamp(5, java.sql.Timestamp.valueOf(transaction.getTimestamp()));
            preparedStatement.executeUpdate();
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                transaction.setTransactionId(generatedKeys.getInt(1));
            }
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
                            resultSet.getDouble("amount"),
                            resultSet.getDouble("balance_after_transaction"),
                            resultSet.getTimestamp("timestamp").toLocalDateTime()
                    );
                } else {
                    return null;
                }
            }
        }
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
                            resultSet.getDouble("amount"),
                            resultSet.getDouble("balance_after_transaction"),
                            resultSet.getTimestamp("timestamp").toLocalDateTime()
                    );
                    transactions.add(transaction);
                }
            }
        }
        return transactions;
    }
}

package com.revature.p0.daos;

import com.revature.p0.models.Account;
import com.revature.p0.models.Transaction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AccountDao {
    private Connection connection;

    public AccountDao(Connection connection) {
        this.connection = connection;
    }

    // Create a new account
    public void createAccount(Account account) throws SQLException {
        String sql = "INSERT INTO accounts (account_id, account_number, user_id, balance) VALUES (?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, account.getAccountId());
            preparedStatement.setString(2, account.getAccountNumber());
            preparedStatement.setInt(3, account.getUserId());
            preparedStatement.setDouble(4, account.getBalance());
            preparedStatement.executeUpdate();
        }
    }

    // Read account by account ID
    public Account getAccountById(int accountId) throws SQLException {
        String sql = "SELECT * FROM accounts WHERE account_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, accountId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return new Account(
                            resultSet.getInt("account_id"),
                            resultSet.getString("account_number"),
                            resultSet.getInt("user_id"),
                            resultSet.getDouble("balance")
                    );
                }
            }
        }
        return null;
    }

    // Get all accounts for a user by user ID
    public List<Account> getAccountsByUserId(int userId) throws SQLException {
        String sql = "SELECT * FROM accounts WHERE user_id = ?";
        List<Account> accounts = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, userId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Account account = new Account(
                            resultSet.getInt("account_id"),
                            resultSet.getString("account_number"),
                            resultSet.getInt("user_id"),
                            resultSet.getDouble("balance")
                    );
                    // Load transactions for each account
                    account.setTransactions(loadTransactions(account.getAccountId()));
                    accounts.add(account);
                }
            }
        }
        return accounts;
    }

    // Update account balance
    public void updateAccountBalance(Account account) throws SQLException {
        String sql = "UPDATE accounts SET balance = ? WHERE account_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setDouble(1, account.getBalance());
            preparedStatement.setInt(2, account.getAccountId());
            preparedStatement.executeUpdate();
        }
    }

    // Delete account by account ID if balance is $0.00
    public void deleteAccount(int accountId) throws SQLException {
        String sql = "DELETE FROM accounts WHERE account_id = ? AND balance = 0.0";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, accountId);
            preparedStatement.executeUpdate();
        }
    }

    // Deposit into account
    public void depositIntoAccount(int accountId, double amount) throws SQLException {
        String sql = "UPDATE accounts SET balance = balance + ? WHERE account_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setDouble(1, amount);
            preparedStatement.setInt(2, accountId);
            preparedStatement.executeUpdate();
        }
    }

    // Withdraw from account
    public void withdrawFromAccount(int accountId, double amount) throws SQLException {
        String sql = "UPDATE accounts SET balance = balance - ? WHERE account_id = ? AND balance >= ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setDouble(1, amount);
            preparedStatement.setInt(2, accountId);
            preparedStatement.setDouble(3, amount);
            preparedStatement.executeUpdate();
        }
    }

    // Transfer from source account to destination account
    public void transferBetweenAccounts(int sourceAccountId, int destinationAccountId, double amount) throws SQLException {
        // Begin transaction
        connection.setAutoCommit(false);
        try {
            // Withdraw from source account
            withdrawFromAccount(sourceAccountId, amount);
            // Deposit into destination account
            depositIntoAccount(destinationAccountId, amount);
            // Commit transaction
            connection.commit();
        } catch (SQLException e) {
            // Rollback transaction on error
            connection.rollback();
            throw e;
        } finally {
            // Restore auto-commit mode
            connection.setAutoCommit(true);
        }
    }

    // Load transactions for an account
    private List<Transaction> loadTransactions(int accountId) throws SQLException {
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

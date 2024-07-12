package com.revature.p0.daos;

import com.revature.p0.models.Account;
import com.revature.p0.models.Transaction;
import com.revature.p0.utils.ConnectionUtil;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AccountDao {
    private final Connection connection;
    private static final Logger logger = LoggerFactory.getLogger(AccountDao.class);

    public AccountDao(Connection connection) {
        this.connection = connection;
    }

    // Create a new account, corrected to use BigDecimal
    public void createAccount(Account account) throws SQLException, IOException {
        String sql = "INSERT INTO accounts (account_number, user_id, balance) VALUES (?, ?, ?)";

        try (Connection conn = ConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, account.getAccountNumber());
            pstmt.setInt(2, account.getUserId());
            pstmt.setBigDecimal(3, account.getBalance());
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating account failed, no rows affected.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    account.setAccountId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating account failed, no ID obtained.");
                }
            }
        } catch (SQLException | IOException e) {
            System.err.println("Unable to create account: " + e.getMessage());
            throw e;
        }
    }

    // Get account by ID
    public Account getAccountById(int accountId) {
        String sql = "SELECT * FROM accounts WHERE account_id = ?";
        Account account = null;
        try (Connection conn = ConnectionUtil.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, accountId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    account = new Account(
                            resultSet.getInt("account_id"),
                            resultSet.getString("account_number"),
                            resultSet.getInt("user_id"),
                            resultSet.getBigDecimal("balance")
                    );
                }
            }
        } catch (SQLException e) {
            logger.error("Error fetching account by ID: {}. SQLState: {}. Error Code: {}.", e.getMessage(), e.getSQLState(), e.getErrorCode(), e);
        } catch (IOException e) {
            logger.error("IO Exception: {}", e.getMessage(), e);
            // Handle IOException here
        }
        if (account == null) {
            logger.info("No account found with ID: {}", accountId);
        }
        return account;
    }

    // Get all accounts for a user by user ID
    public List<Account> getAccountsByUserId(int userId) {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT * FROM accounts WHERE user_id = ?";
        try (Connection conn = ConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Account account = new Account();
                    // Assuming Account class has these setters
                    account.setAccountId(rs.getInt("account_id"));
                    account.setUserId(rs.getInt("user_id"));
                    account.setAccountNumber(rs.getString("account_number"));
                    account.setBalance(rs.getBigDecimal("balance"));
                    accounts.add(account);
                }
            }
        } catch (SQLException | IOException e) {
            System.err.println("Error fetching accounts by user ID: " + e.getMessage());
            System.err.println("SQLState: " + e.getMessage());
        }
        return accounts;
    }

    // Update account balance
    public void updateAccountBalance(Account account, Connection connection) {
        String sql = "UPDATE accounts SET balance = ? WHERE account_id = ?";
        try (Connection conn = ConnectionUtil.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setBigDecimal(1, account.getBalance());
            preparedStatement.setInt(2, account.getAccountId());
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected == 0) {
                logger.info("No account updated. The account does not exist or another condition prevents update.");
            }
        } catch (SQLException | IOException e) {
            logger.error("Database error: This connection has been closed.", e);
        }
    }

    public void deleteAccount(int accountId) throws SQLException, IOException {
        String sql = "DELETE FROM accounts WHERE account_id = ? AND balance >= 0.0";
        try (Connection conn = ConnectionUtil.getConnection(); // Obtain a new connection
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, accountId);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected == 0) {
                logger.info("No account deleted. The account does not exist, has a negative balance, or another condition prevents deletion.");
            } else {
                logger.info("Account with ID {} and non-negative balance successfully deleted.", accountId);
            }
        } catch (SQLException e) {
            logger.error("SQL Exception during account deletion: {}. SQLState: {}. Error Code: {}.", e.getMessage(), e.getSQLState(), e.getErrorCode(), e);
            throw e;
        } catch (IOException e) {
            logger.error("IO Exception during account deletion: {}", e.getMessage(), e);
            throw e;
        }
    }

    // Deposit into account
    public void depositIntoAccount(int accountId, BigDecimal amount) {
        String sql = "UPDATE accounts SET balance = balance + ? WHERE account_id = ?";
        try (Connection conn = ConnectionUtil.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            conn.setAutoCommit(false); // Start transaction block

            preparedStatement.setBigDecimal(1, amount);
            preparedStatement.setInt(2, accountId);
            int rowsUpdated = preparedStatement.executeUpdate();
            logger.info("Deposit successful. Rows updated: {}", rowsUpdated);

            // Correctly instantiate TransactionDao with the current connection
            TransactionDao transactionDao = new TransactionDao(conn);
            // Adjusted to match the corrected Transaction constructor
            Transaction transaction = new Transaction(accountId, "deposit", amount); // Assuming constructor without Account parameter
            transactionDao.createTransaction(transaction, connection); // Now this should work without throwing a NullPointerException

            conn.commit(); // Commit transaction
        } catch (SQLException | IOException e) {
            logger.error("Deposit failed: {}", e.getMessage());
        }
    }

    // Withdraw from account
    public void withdrawFromAccount(int accountId, BigDecimal amount) throws SQLException, IOException {
        String sql = "UPDATE accounts SET balance = balance - ? WHERE account_id = ? AND balance >= ?";
        try (Connection conn = ConnectionUtil.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setBigDecimal(1, amount);
            preparedStatement.setInt(2, accountId);
            preparedStatement.setBigDecimal(3, amount);
            preparedStatement.executeUpdate();
        } catch (SQLException | IOException e) {
            throw e;
        }
    }

    // Transfer from source account to destination account
    public void transferBetweenAccounts(int sourceAccountId, int destinationAccountId, BigDecimal amount) {
        // Validate input parameters here
        Connection conn = null;
        try {
            conn = ConnectionUtil.getConnection();
            conn.setAutoCommit(false); // Start transaction

            // Withdraw from source account
            withdrawFromAccount(sourceAccountId, amount);
            // Deposit into destination account
            depositIntoAccount(destinationAccountId, amount);

            conn.commit(); // Commit transaction
            logger.info("Transfer successful from account {} to account {} of amount {}", sourceAccountId, destinationAccountId, amount);
        } catch (SQLException | IOException e) {
            try {
                if (conn != null) {
                    conn.rollback(); // Rollback transaction on error
                }
                logger.error("Transfer failed, transaction rolled back. Error: {}", e.getMessage());
            } catch (SQLException rollbackEx) {
                logger.error("Error rolling back transaction: {}", rollbackEx.getMessage());
            }
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true); // Reset auto-commit
                    conn.close(); // Close connection
                }
            } catch (SQLException e) {
                logger.error("Error resetting auto-commit or closing connection: {}", e.getMessage());
            }
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
                            resultSet.getBigDecimal("amount")
                    );
                    transactions.add(transaction);
                }
            }
        }
        return transactions;
    }
}

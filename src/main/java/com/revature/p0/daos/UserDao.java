package com.revature.p0.daos;

import java.sql.*;
import java.util.logging.Logger;

import com.revature.p0.models.User;
import com.revature.p0.utils.ConnectionUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UserDao {
    private static final Logger LOGGER = Logger.getLogger(UserDao.class.getName());
    private final Connection connection;

    public UserDao(Connection connection) {
        this.connection = connection;
    }

    public void createUser(User user) throws SQLException, IOException {
        String sql = "INSERT INTO users (first_name, last_name, username, password, phone_number) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, user.getFirstName());
            pstmt.setString(2, user.getLastName());
            pstmt.setString(3, user.getUsername());
            pstmt.setString(4, user.getPassword());
            pstmt.setString(5, user.getPhoneNumber());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating user failed, no rows affected.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setUserId(generatedKeys.getInt(1));
                    // Print the generated user ID
                    System.out.println("Created user with ID: " + user.getUserId());
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
        } catch (SQLException | IOException e) {
            System.err.println("Unable to create user: " + e.getMessage());
            throw e;
        }
    }


    // Read user by user ID
    public User getUserById(int userId) throws SQLException {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, userId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    User user = new User();
                    user.setUserId(resultSet.getInt("user_id"));
                    user.setUsername(resultSet.getString("username"));
                    user.setPassword(resultSet.getString("password"));
                    return user;
                }
            }
        }
        return null;
    }

    // Update user password
    public void updateUserPassword(User user) throws SQLException {
        String sql = "UPDATE users SET password = ? WHERE user_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, user.getPassword());
            preparedStatement.setInt(2, user.getUserId());
            preparedStatement.executeUpdate();
        }
    }

    // Delete user by user ID
    public void deleteUser(int userId) throws SQLException {
        String sql = "DELETE FROM users WHERE user_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, userId);
            preparedStatement.executeUpdate();
        }
    }

    // Retrieve a user by username
    public User getUserByUsername(String username) throws SQLException, IOException {
        String sql = "SELECT * FROM users WHERE username = ?";
        User user = null;
        try (Connection conn = ConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    user = new User();
                    user.setUserId(rs.getInt("user_id"));
                    user.setFirstName(rs.getString("first_name"));
                    user.setLastName(rs.getString("last_name"));
                    user.setUsername(rs.getString("username"));
                    user.setPassword(rs.getString("password"));
                    user.setPhoneNumber(rs.getString("phone_number"));
                    // Assuming there's a method to load user's accounts
                    // user.setAccounts(loadUserAccounts(user.getUserId()));
                }
            }
        }
        return user;
    }

    // Retrieve all users from the database
    public List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    User user = new User();
                    user.setUserId(resultSet.getInt("user_id"));
                    user.setUsername(resultSet.getString("username"));
                    user.setPassword(resultSet.getString("password"));
                    users.add(user);
                }
            }
        }
        return users;
    }

    // Update user information
    public void updateUser(User user) throws SQLException, IOException {
        String sql = "UPDATE users SET username = ?, password = ? WHERE user_id = ?";
        try (Connection conn = ConnectionUtil.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getPassword());
            preparedStatement.setInt(3, user.getUserId());
            preparedStatement.executeUpdate();
        } catch (SQLException | IOException e) {
            LOGGER.severe("Update failed: " + e.getMessage());
            throw e;
        }
    }
}

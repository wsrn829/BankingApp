package com.revature.p0.services;

import com.revature.p0.daos.UserDao;
import com.revature.p0.models.User;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class UserService {
    private final UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    // Create/Register a new user
    public void registerUser(User user) throws SQLException, IOException {
        userDao.createUser(user);
    }

    // Get a user by ID
    public User getUserById(int userId) throws SQLException {
        return userDao.getUserById(userId);
    }

    // Get a user by username
    public Optional<User> getUserByUsername(String username) throws SQLException, IOException {
        return Optional.ofNullable(userDao.getUserByUsername(username));
    }

    // Get all users
    public List<User> getAllUsers() throws SQLException {
        return userDao.getAllUsers();
    }

    // Update user information
    public void updateUser(User user) throws SQLException, IOException {
        userDao.updateUser(user);
    }

    // Delete a user by ID
    public void deleteUser(int userId) throws SQLException {
        userDao.deleteUser(userId);
    }

    // User login
    public boolean userLogin(String username, String password) throws SQLException, IOException {
        User user = userDao.getUserByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            return true;
        }
        return false;
    }
}

package com.revature.p0.controllers;

import com.revature.p0.models.User;
import com.revature.p0.services.UserService;
import io.javalin.Javalin;
import io.javalin.http.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class UserController {
    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    public UserController(UserService userService) {
        this.userService = userService;
    }

    public void registerEndpoints(Javalin app) {
        app.post("/users", this::registerUser);
        app.get("/users/{userId}", this::getUserById);
        app.get("/users/username/{username}", this::getUserByUsername);
        app.get("/users", this::getAllUsers);
        app.put("/users/{userId}", this::updateUser);
        app.delete("/users/{userId}", this::deleteUser);
        app.post("/users/login", this::userLogin);
    }

    private void registerUser(Context ctx) {
        try {
            User user = ctx.bodyAsClass(User.class);
            userService.registerUser(user);
            ctx.status(201).json(user);
        } catch (SQLException e) {
            logger.error("Error registering user: {}", e.getMessage());
            ctx.status(500).json("Error registering user");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void getUserById(Context ctx) {
        try {
            int userId = Integer.parseInt(ctx.pathParam("userId"));
            User user = userService.getUserById(userId);
            if (user != null) {
                ctx.json(user);
            } else {
                ctx.status(404).result("User not found");
            }
        } catch (NumberFormatException | SQLException e) {
            ctx.status(400).result("Invalid user ID format or database error");
        }
    }

    private void getUserByUsername(Context ctx) {
        try {
            String username = ctx.pathParam("username");
            userService.getUserByUsername(username).ifPresentOrElse(
                    ctx::json,
                    () -> ctx.status(404).result("User not found")
            );
        } catch (SQLException e) {
            ctx.status(500).result("Database error");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void getAllUsers(Context ctx) {
        try {
            List<User> users = userService.getAllUsers();
            ctx.json(users);
        } catch (SQLException e) {
            ctx.status(500).result("Database error");
        }
    }

    private void updateUser(Context ctx) {
        try {
            int userId = Integer.parseInt(ctx.pathParam("userId"));
            User user = ctx.bodyAsClass(User.class);
            user.setUserId(userId);
            userService.updateUser(user);
            ctx.status(204);
        } catch (NumberFormatException | SQLException e) {
            ctx.status(400).result("Invalid user ID format or database error");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void deleteUser(Context ctx) {
        try {
            int userId = Integer.parseInt(ctx.pathParam("userId"));
            userService.deleteUser(userId);
            ctx.status(204);
        } catch (NumberFormatException | SQLException e) {
            ctx.status(400).result("Invalid user ID format or database error");
        }
    }

    private void userLogin(Context ctx) {
        try {
            User user = ctx.bodyAsClass(User.class);
            boolean isAuthenticated = userService.userLogin(user.getUsername(), user.getPassword());
            if (isAuthenticated) {
                ctx.status(200).result("Login successful");
            } else {
                ctx.status(401).result("Invalid credentials");
            }
        } catch (SQLException e) {
            ctx.status(500).result("Database error: " + e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    }

package com.revature.p0.controllers;

import com.revature.p0.services.UserService;
import com.revature.p0.models.User;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.sql.SQLException;

public class UserController {
    UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    public void registerEndpoints(Javalin app) {
        app.post("/users", this::registerUser);
        app.get("/users/{userId}", this::getUserById);
        app.get("/users/username/{username}", this::getUserByUsername);
        app.get("/users", this::getAllUsers);
        app.put("/users/{userId}", this::updateUser);
        app.delete("users/{userId}", this::deleteUser);
        app.post("users/login", this::userLogin);
    }

    private void registerUser(Context ctx) throws SQLException {
        User user = ctx.bodyAsClass(User.class);
        userService.registerUser(user);
        ctx.status(201).json(user);
    }

    private void getUserById(Context ctx) throws SQLException {
        int userId = Integer.parseInt(ctx.pathParam("userId"));
        User user = userService.getUserById(userId);
        if (user != null) {
            ctx.json(user);
        } else {
            ctx.status(404).result("User not found");
        }
    }

    private void getUserByUsername(Context ctx) throws SQLException {
        String username = ctx.pathParam("username");
        User user = userService.getUserByUsername(username);
        if (user != null) {
            ctx.json(user);
        } else {
            ctx.status(404).result("User not found");
        }
    }

    private void getAllUsers(Context ctx) throws SQLException {
        ctx.json(userService.getAllUsers())
;    }

    private void updateUser(Context ctx) throws SQLException {
        int userId = Integer.parseInt(ctx.pathParam("userId"));
        User user = ctx.bodyAsClass(User.class);
        user.setUserId(userId);
        userService.updateUser(user);
        ctx.status(204);
    }

    private void deleteUser(Context ctx) throws SQLException {
        int userId = Integer.parseInt(ctx.pathParam("userId"));
        userService.deleteUser(userId);
        ctx.status(204);
    }

    private void userLogin(Context ctx) throws SQLException {
        User user = ctx.bodyAsClass(User.class);
        boolean authenticateUser = userService.userLogin(user.getUsername(), user.getPassword());
        if (authenticateUser) {
            ctx.status(200).result("Login successful");
        } else {
            ctx.status(401).result("Invalid credentials");
        }
    }
}


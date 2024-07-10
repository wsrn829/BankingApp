package com.revature.p0;

import com.revature.p0.controllers.UserController;
import com.revature.p0.daos.UserDao;
import com.revature.p0.services.UserService;
import com.revature.p0.utils.ConnectionUtil;
import io.javalin.Javalin;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class Driver {
    public static void main(String[] args) {
        try (Connection connection = ConnectionUtil.getConnection()) {
            UserDao userDao = new UserDao(connection);
            UserService userService = new UserService(userDao);
            UserController userController = new UserController(userService);

            Javalin app = Javalin.create().start(8080);
            System.out.println("Server is running on http://localhost:8080");

            userController.registerEndpoints(app);
        } catch (SQLException | IOException e) {
            System.err.println("Unable to connect to the database: " + e.getMessage());
        }
    }
}

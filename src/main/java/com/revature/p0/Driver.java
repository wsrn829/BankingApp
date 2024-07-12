package com.revature.p0;

import com.revature.p0.controllers.AccountController;
import com.revature.p0.controllers.TransactionController;
import com.revature.p0.controllers.UserController;
import com.revature.p0.daos.AccountDao;
import com.revature.p0.daos.TransactionDao;
import com.revature.p0.daos.UserDao;
import com.revature.p0.services.AccountService;
import com.revature.p0.services.TransactionService;
import com.revature.p0.services.UserService;
import com.revature.p0.utils.ConnectionUtil;
import io.javalin.Javalin;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class Driver {
    public static void main(String[] args) {
        try (Connection connection = ConnectionUtil.getConnection()) {
            // Initialize UserDao and UserService
            UserDao userDao = new UserDao(connection);
            UserService userService = new UserService(userDao);
            UserController userController = new UserController(userService);

            // Initialize AccountDao, TransactionDao, AccountService, and TransactionService
            AccountDao accountDao = new AccountDao(connection);
            TransactionDao transactionDao = new TransactionDao(connection);
            AccountService accountService = new AccountService(accountDao, transactionDao, connection);
            TransactionService transactionService = new TransactionService(transactionDao);

            // Initialize AccountController and TransactionController
            AccountController accountController = new AccountController(accountService);
            TransactionController transactionController = new TransactionController(transactionService);

            // Create a new Javalin application and start it on port 8080
            Javalin app = Javalin.create().start(8080);
            System.out.println("Server is running on http://localhost:8080");

            // Register endpoints for UserController, AccountController, and TransactionController
            userController.registerEndpoints(app);
            accountController.registerEndpoints(app);
            transactionController.registerEndpoints(app);
        } catch (SQLException | IOException e) {
            // Handle exceptions related to database connection
            System.err.println("Unable to connect to the database: " + e.getMessage());
        }
    }
}

package com.revature.p0.utils;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionUtil {
    public static Connection getConnection() throws SQLException, IOException {
        Properties props = new Properties();
        try (InputStream inputStream = ConnectionUtil.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (inputStream != null) {
                props.load(inputStream);
            } else {
                throw new IOException("Unable to find application.properties");
            }
        }

        Connection conn = DriverManager.getConnection(
                props.getProperty("url"),
                props.getProperty("username"),
                props.getProperty("password")
        );
        System.out.println("Database connection established successfully.");
        return conn;
    }
}
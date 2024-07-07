package com.revature.p0.daos;

import com.revature.p0.models.User;
import com.revature.p0.utils.ConnectionUtil;

import java.io.IOException;
import java.sql.*;

public class UserDao {
    Connection connection;

    public UserDao(Connection connection) throws SQLException, IOException {
        this.connection = ConnectionUtil.getConnection();
    }



}

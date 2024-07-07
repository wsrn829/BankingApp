package com.revature.p0;

import com.revature.p0.daos.UserDao;
import com.revature.p0.menus.LoginMenu;
import com.revature.p0.menus.MainMenu;
import com.revature.p0.menus.MenuUtil;
import com.revature.p0.menus.RegisterMenu;
import com.revature.p0.utils.ConnectionUtil;

import java.io.IOException;
import java.sql.SQLException;

public class Driver {
    public static void main(String[] args) {
//        MenuUtil menuUtil = MenuUtil.getMenuUtil();
//        menuUtil.register(new MainMenu());
//        menuUtil.register(new RegisterMenu());
//        menuUtil.register(new LoginMenu());
//        menuUtil.run();

        try {
            UserDao userDao = new UserDao(ConnectionUtil.getConnection());
            userDao.dropTable();
            userDao.createTable();
            userDao.populateTable();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
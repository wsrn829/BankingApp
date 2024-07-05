package com.revature.p0.services;

import com.revature.p0.daos.UserDao;

public class UserService {
    UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }
}

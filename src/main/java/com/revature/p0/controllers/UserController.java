package com.revature.p0.controllers;

import com.revature.p0.services.UserService;

public class UserController {
    UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }
}


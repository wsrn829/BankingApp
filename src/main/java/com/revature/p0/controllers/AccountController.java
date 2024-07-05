package com.revature.p0.controllers;

import com.revature.p0.services.AccountService;

public class AccountController {
    AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }
}

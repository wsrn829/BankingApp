package com.revature.p0.services;

import com.revature.p0.daos.AccountDao;

public class AccountService {
    AccountDao accountDao;

    public AccountService(AccountDao accountDao) {
        this.accountDao = accountDao;
    }
}
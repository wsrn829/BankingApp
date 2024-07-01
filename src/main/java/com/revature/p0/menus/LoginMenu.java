package com.revature.p0.menus;

import com.revature.p0.utils.FakeDatabase;

import java.util.Scanner;

public class LoginMenu implements Menu{
    String name = "login";
    MenuUtil menuUtil = MenuUtil.getMenuUtil();
    Scanner sc = menuUtil.getScanner();

    public void render() {
        System.out.println("=======[ Login ]=======");
        System.out.print("Enter username: ");
        String username = sc.nextLine();

        System.out.print("Enter password: ");
        String password = sc.nextLine();

        if(username.equals(FakeDatabase.getDatabase().read("username"))
            && password.equals(FakeDatabase.getDatabase().read("password"))) {
            System.out.println("Successful login! Demo over!");
        } else {
            System.out.println("Unable to login, bad username and/or password. Demo over!");
        }

        menuUtil.quit();
    }

    public String getName() {
        return this.name;
    }
}


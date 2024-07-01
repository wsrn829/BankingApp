package com.revature.p0.menus;

import com.revature.p0.utils.FakeDatabase;

import java.util.Scanner;

public class RegisterMenu implements Menu{
    String name = "register";
    MenuUtil menuUtil = MenuUtil.getMenuUtil();
    Scanner sc = menuUtil.getScanner();

    public void render() {
        System.out.println("=======[ Register ]=======");
        System.out.print("Enter a new username: ");
        String username = sc.nextLine();

        System.out.print("Enter a new password: ");
        String password1 = sc.nextLine();

        System.out.print("Re-enter password: ");
        String password2 = sc.nextLine();

        if(password1.equals(password2)) {
            System.out.println("New account created successfully!");
            FakeDatabase.getDatabase().write("username", username);
            FakeDatabase.getDatabase().write("password", password1);
            menuUtil.navigate("login");
        } else {
            System.out.println("Passwords don't match!");
            menuUtil.quit();
        }

    }

    public String getName() {
        return this.name;
    }
}

package com.revature.p0.menus;

import java.util.Scanner;

public class MainMenu implements Menu {
    String name = "main";
    MenuUtil menuUtil = MenuUtil.getMenuUtil();
    Scanner sc = menuUtil.getScanner();

    public void render() {
        //The render method should just be a collection of prompts and console inputs
        System.out.println("=======[ Main Menu ]=======");
        System.out.println("R) Register\nL) Login\nQ) Quit");
        String input = sc.nextLine();
        switch(input) {
            case "R":
            case "r":
                menuUtil.navigate("register");
                break;
            case "L":
            case "l":
                menuUtil.navigate("login");
                break;
            case "Q":
            case "q":
                menuUtil.quit();
                break;
        }

    }

    public String getName() {
        return this.name;
    }
}

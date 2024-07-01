package com.revature.p0.menus;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MenuUtil {
    private String startingMenu = "main";
    private boolean running = true;
    private Menu next;
    private final List<Menu> menuList = new ArrayList<>();
    private static MenuUtil singleton;
    private Scanner sc = new Scanner(System.in);

    private MenuUtil() {
    }

    public static MenuUtil getMenuUtil() {
        if(singleton == null) {
            singleton = new MenuUtil();
        }
        return singleton;
    }

    public void run() {
        navigate(startingMenu);
        while(running) {
            this.next.render();
        }
    }

    public void navigate(String menuName) {
        for(Menu m : menuList) {
            if(menuName.equals(m.getName())) {
                this.next = m;
                break;
            }
        }
    }

    public void register(Menu menu) {
        menuList.add(menu);
    }

    public void register(List<Menu> menuList) {
        this.menuList.addAll(menuList);
    }

    public void quit() {
        this.running = false;
    }

    public Scanner getScanner() {
        return this.sc;
    }
}

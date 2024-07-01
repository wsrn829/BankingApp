package com.revature.p0.utils;

import java.util.HashMap;
import java.util.Map;

public class FakeDatabase {
    private final Map<String, String> fakeDatabase;
    private static FakeDatabase singleton;

    private FakeDatabase() {
        this.fakeDatabase =  new HashMap<>();
    }

    public static FakeDatabase getDatabase() {
        if(singleton == null) {
            singleton = new FakeDatabase();
        }
        return singleton;
    }

    public void write(String key, String value) {
        fakeDatabase.put(key, value);
    }

    public String read(String key) {
        return fakeDatabase.get(key);
    }

}

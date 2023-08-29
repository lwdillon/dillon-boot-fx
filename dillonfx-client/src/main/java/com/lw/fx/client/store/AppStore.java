package com.lw.fx.client.store;

import org.kordamp.ikonli.feather.Feather;

import java.util.Random;

public class AppStore {
    protected static final Random RANDOM = new Random();

    private static String token;

    public static String getToken() {
        return token;
    }

    public static void setToken(String token) {
        AppStore.token = token;
    }


    public static Feather randomIcon() {
        return Feather.values()[RANDOM.nextInt(Feather.values().length)];
    }
}

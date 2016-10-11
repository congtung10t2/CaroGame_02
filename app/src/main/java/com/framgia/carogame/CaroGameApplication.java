package com.framgia.carogame;

import android.app.Application;

/**
 * Created by framgia on 10/10/2016.
 */
public class CaroGameApplication extends Application {
    private static CaroGameApplication instance;

    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static CaroGameApplication getInstance(){
        return instance;
    }
}
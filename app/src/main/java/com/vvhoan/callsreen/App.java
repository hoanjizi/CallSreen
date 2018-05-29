package com.vvhoan.callsreen;

import android.app.Application;

import timber.log.Timber;

/**
 * Created by vvhoan on 5/29/2018.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Timber.plant(new Timber.DebugTree());
    }
}

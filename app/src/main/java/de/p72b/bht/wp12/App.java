package de.p72b.bht.wp12;

import android.annotation.SuppressLint;
import android.app.Application;

public final class App extends Application {
    @SuppressLint("StaticFieldLeak")
    private static App sInstance;

    public static App getInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
    }
}

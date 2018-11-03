package de.p72b.bht.wp12.Service;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

public class Settings {

    private SharedPreferences mSharedPref;

    Settings(@NonNull Context context) {
        mSharedPref = context.getSharedPreferences("app.settings", Context.MODE_PRIVATE);
    }

    public void writeToPreferences(@NonNull final String key, boolean value) {
        final SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public boolean readFomPreferences(@NonNull final String key) {
        return mSharedPref.getBoolean(key, false);
    }
}
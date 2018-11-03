package de.p72b.bht.wp12.location;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;

import de.p72b.bht.wp12.Service.AppServices;
import de.p72b.bht.wp12.Service.Settings;

@SuppressLint("Registered")
public class BaseLocationAwareActivity extends FragmentActivity {

    protected LocationManager mLocationManager;
    private Settings mSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSettings = AppServices.getService(AppServices.SETTINGS);
        mLocationManager = new LocationManager(this, mSettings);
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode,
                                           @NonNull final String[] permissions,
                                           @NonNull final int[] grantResults) {
        for (String permission: permissions) {
            mSettings.writeToPreferences(permission, true);
        }
    }
}

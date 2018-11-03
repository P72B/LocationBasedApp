package de.p72b.bht.wp12.location;

import android.app.Activity;
import android.support.annotation.NonNull;

import de.p72b.bht.wp12.Service.Settings;

public class LocationManager {

    private final GooglePlayServicesLocationSource mFusedLocationSource;

    LocationManager(@NonNull final Activity activity, @NonNull final Settings settings,
                    @NonNull final SettingsClientManager settingsClientManager) {
        final PermissionManager permissionManager = new PermissionManager(activity, settings);
        mFusedLocationSource = new GooglePlayServicesLocationSource(activity, permissionManager,
                settingsClientManager);
    }

    public void getLastLocation(@NonNull final ILastLocationListener listener) {
        mFusedLocationSource.getLastLocation(listener);
    }
}

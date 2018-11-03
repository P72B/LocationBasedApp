package de.p72b.bht.wp12.location;

import android.app.Activity;
import android.support.annotation.NonNull;

import de.p72b.bht.wp12.Service.Settings;

public class LocationManager {

    private final GooglePlayServicesLocationSource mFusedLocationSource;
    private final PermissionManager mPermissionManager;

    LocationManager(@NonNull final Activity activity, @NonNull final Settings settings) {
        mPermissionManager = new PermissionManager(activity, settings);
        mFusedLocationSource = new GooglePlayServicesLocationSource(activity, mPermissionManager);
    }

    public void getLastLocation(@NonNull final ILastLocationListener listener) {
        mFusedLocationSource.getLastLocation(listener);
    }
}

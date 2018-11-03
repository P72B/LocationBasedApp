package de.p72b.bht.wp12.location;

import android.app.Activity;
import android.support.annotation.NonNull;

public class LocationManager {

    private final GooglePlayServicesLocationSource mFusedLocationSource;

    public LocationManager(@NonNull final Activity activity) {
        mFusedLocationSource = new GooglePlayServicesLocationSource(activity);
    }

    public void getLastLocation(@NonNull final ILastLocationListener listener) {
        mFusedLocationSource.getLastLocation(listener);
    }
}

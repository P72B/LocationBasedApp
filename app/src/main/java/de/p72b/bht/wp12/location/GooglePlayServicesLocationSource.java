package de.p72b.bht.wp12.location;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.location.Location;
import android.support.annotation.NonNull;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

class GooglePlayServicesLocationSource {

    private final Activity mActivity;
    private final FusedLocationProviderClient mFusedLocationClient;

    GooglePlayServicesLocationSource(@NonNull final Activity activity) {
        mActivity = activity;
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mActivity);
    }

    @SuppressLint("MissingPermission")
    public void getLastLocation(@NonNull final ILastLocationListener listener) {
        final Task<Location> getLastLocationTask = mFusedLocationClient.getLastLocation();
        getLastLocationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    listener.onLastLocationSuccess(location);
                } else {
                    listener.onLastLocationFailure("Location is null");
                }
            }
        });
        getLastLocationTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                listener.onLastLocationFailure(e.getMessage());
            }
        });
    }
}

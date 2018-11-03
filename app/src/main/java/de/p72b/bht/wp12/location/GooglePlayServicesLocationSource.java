package de.p72b.bht.wp12.location;

import android.Manifest;
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
    private final PermissionManager mPermissionManager;

    GooglePlayServicesLocationSource(@NonNull final Activity activity,
                                     @NonNull final PermissionManager permissionManager) {
        mActivity = activity;
        mPermissionManager = permissionManager;
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mActivity);
    }

    @SuppressLint("MissingPermission")
    void getLastLocation(@NonNull final ILastLocationListener listener) {
        if (!mPermissionManager.hasPermissionIfNotRequest(Manifest.permission.ACCESS_FINE_LOCATION)) {
            listener.onLastLocationFailure("Location permission missing");
            return;
        }

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

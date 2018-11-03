package de.p72b.bht.wp12.location;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.location.Location;
import android.support.annotation.NonNull;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

class GooglePlayServicesLocationSource {

    private final Activity mActivity;
    private final FusedLocationProviderClient mFusedLocationClient;
    private final PermissionManager mPermissionManager;
    private final SettingsClientManager mSettingsClientManager;
    private LocationRequest mLocationRequest;

    GooglePlayServicesLocationSource(@NonNull final Activity activity,
                                     @NonNull final PermissionManager permissionManager,
                                     @NonNull final SettingsClientManager settingsClientManager) {
        mActivity = activity;
        mPermissionManager = permissionManager;
        mSettingsClientManager = settingsClientManager;
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mActivity);
        initLocationRequest();
    }

    void getLastLocation(@NonNull final ILastLocationListener listener) {
        if (!mPermissionManager.hasPermissionIfNotRequest(Manifest.permission.ACCESS_FINE_LOCATION)) {
            listener.onLastLocationFailure("Location permission missing");
            return; // early return here
        }

        mSettingsClientManager.checkIfDeviceLocationSettingFulfillRequestRequirements(
                true, mLocationRequest, new ISettingsClientResultListener() {
                    @Override
                    public void onSuccess() {
                        getLastFusedLocation(listener);
                    }

                    @Override
                    public void onFailure(@NonNull String message) {
                        listener.onLastLocationFailure(message);
                    }
                });
    }

    @SuppressLint("MissingPermission")
    private void getLastFusedLocation(@NonNull final ILastLocationListener listener) {
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

    private void initLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10_000);
        mLocationRequest.setFastestInterval(5_000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }
}

package de.p72b.bht.wp12.location;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

class GooglePlayServicesLocationSource {

    private final FusedLocationProviderClient mFusedLocationClient;
    private final PermissionManager mPermissionManager;
    private final SettingsClientManager mSettingsClientManager;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private ILocationUpdatesListener mLocationUpdatesListener;

    GooglePlayServicesLocationSource(@NonNull final Activity activity,
                                     @NonNull final PermissionManager permissionManager,
                                     @NonNull final SettingsClientManager settingsClientManager,
                                     @Nullable final ILocationUpdatesListener listener) {
        mPermissionManager = permissionManager;
        mSettingsClientManager = settingsClientManager;
        mLocationUpdatesListener = listener;
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
        initLocationRequest();
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@Nullable final LocationResult locationResult) {
                if (locationResult == null || mLocationUpdatesListener == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    mLocationUpdatesListener.onLocationChanged(location);
                }
            }
        };
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

    void startReceivingLocationUpdates() {
        if (!mPermissionManager.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                || mLocationUpdatesListener == null) {
            return;
        }

        mSettingsClientManager.checkIfDeviceLocationSettingFulfillRequestRequirements(
                false, mLocationRequest, new ISettingsClientResultListener() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onSuccess() {
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                mLocationCallback, null);
                    }

                    @Override
                    public void onFailure(@NonNull final String message) {
                        // it makes no sense to start location updates without proper settings.
                    }
                });
    }

    void stopReceivingLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    @SuppressLint("MissingPermission")
    private void getLastFusedLocation(@NonNull final ILastLocationListener listener) {
        final Task<Location> getLastLocationTask = mFusedLocationClient.getLastLocation();
        getLastLocationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(@Nullable Location location) {
                listener.onLastLocationSuccess(location);
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

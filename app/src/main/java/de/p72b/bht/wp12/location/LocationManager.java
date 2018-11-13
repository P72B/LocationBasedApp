package de.p72b.bht.wp12.location;

import android.Manifest;
import android.app.Activity;
import android.location.Location;
import android.support.annotation.NonNull;

import java.util.concurrent.CopyOnWriteArrayList;

import de.p72b.bht.wp12.Service.Settings;

public class LocationManager implements ILocationUpdatesListener {

    private final GooglePlayServicesLocationSource mFusedLocationSource;
    private CopyOnWriteArrayList<ILocationUpdatesListener> mSubscribers =
            new CopyOnWriteArrayList<>();
    private PermissionManager mPermissionManager;

    LocationManager(@NonNull final Activity activity, @NonNull final Settings settings,
                    @NonNull final SettingsClientManager settingsClientManager) {
        mPermissionManager = new PermissionManager(activity, settings);
        mFusedLocationSource = new GooglePlayServicesLocationSource(activity, mPermissionManager,
                settingsClientManager, this);
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        if (mSubscribers.isEmpty()) {
            return;
        }
        for (ILocationUpdatesListener listener : mSubscribers) {
            listener.onLocationChanged(location);
        }
    }

    public void getLastLocation(@NonNull final ILastLocationListener listener) {
        mFusedLocationSource.getLastLocation(listener);
    }

    public void subscribeToLocationChanges(ILocationUpdatesListener listener) {
        if (mSubscribers.isEmpty()) {
            mFusedLocationSource.startReceivingLocationUpdates();
        }
        mSubscribers.add(listener);
    }

    public void unSubscribeToLocationChanges(ILocationUpdatesListener listener) {
        mSubscribers.remove(listener);
        if (mSubscribers.isEmpty()) {
            mFusedLocationSource.stopReceivingLocationUpdates();
        }
    }

    public boolean hasLocationPermission() {
        return mPermissionManager.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION);
    }
}

package de.p72b.bht.wp12.location;

import android.Manifest;
import android.app.Activity;
import android.location.Location;
import android.support.annotation.NonNull;

import com.google.android.gms.location.LocationRequest;

import java.util.concurrent.CopyOnWriteArrayList;

import de.p72b.bht.wp12.App;
import de.p72b.bht.wp12.Service.AppServices;
import de.p72b.bht.wp12.Service.Settings;
import de.p72b.bht.wp12.http.IWebService;

public class LocationManager implements ILocationUpdatesListener {

    private final GooglePlayServicesLocationSource mFusedLocationSource;
    private final SettingsClientManager mSettingsClientManager;
    private final WifiGeolocationSource mWifiGeolocationSource;
    private CopyOnWriteArrayList<ILocationUpdatesListener> mSubscribers =
            new CopyOnWriteArrayList<>();
    private PermissionManager mPermissionManager;

    LocationManager(@NonNull final Activity activity, @NonNull final Settings settings,
                    @NonNull final SettingsClientManager settingsClientManager) {
        mSettingsClientManager = settingsClientManager;
        mPermissionManager = new PermissionManager(activity, settings);
        mFusedLocationSource = new GooglePlayServicesLocationSource(activity, mPermissionManager,
                settingsClientManager, this);

        IWebService webService = AppServices.getService(AppServices.WEB);
        mWifiGeolocationSource = new WifiGeolocationSource(webService, mPermissionManager);
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

    public void getWifiBasedLocation(@NonNull final ILastLocationListener listener) {
        mWifiGeolocationSource.scanForWifi(listener);
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

    void notifyPermissionRequestResults(String[] permissions, int[] grantResults) {
        int index = 0;
        for (String permission: permissions) {
            switch(permission) {
                case Manifest.permission_group.LOCATION:
                case Manifest.permission.ACCESS_COARSE_LOCATION:
                case Manifest.permission.ACCESS_FINE_LOCATION:
                    int grantResult = grantResults[index];
                    if (Activity.RESULT_OK == grantResult) {
                        mFusedLocationSource.startReceivingLocationUpdates();
                    } else if (Activity.RESULT_CANCELED == grantResult) {
                        mFusedLocationSource.stopReceivingLocationUpdates();
                    }
                    break;
            }
            index++;
        }
    }

    public void deviceLocationSettingFulfilled(@NonNull final ISettingsClientResultListener listener) {
        final LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10_000);
        locationRequest.setFastestInterval(5_000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mSettingsClientManager.checkIfDeviceLocationSettingFulfillRequestRequirements(
                false, locationRequest, listener);
    }
}

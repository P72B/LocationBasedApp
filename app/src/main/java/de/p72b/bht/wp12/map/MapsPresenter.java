package de.p72b.bht.wp12.map;

import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import de.p72b.bht.wp12.R;
import de.p72b.bht.wp12.location.ILastLocationListener;
import de.p72b.bht.wp12.location.ILocationUpdatesListener;
import de.p72b.bht.wp12.location.ISettingsClientResultListener;
import de.p72b.bht.wp12.location.LocationManager;

class MapsPresenter implements ILocationUpdatesListener {

    private LocationManager mLocationManager;
    private IMapsView mView;
    private boolean mFollowLocation = false;
    private boolean mIsGpsBlocked = true;

    MapsPresenter(@NonNull final FragmentActivity fragmentActivity,
                  @NonNull final LocationManager locationManager) {
        mLocationManager = locationManager;
        mView = (IMapsView) fragmentActivity;
        initLocateMeFabIcon();
    }

    private void setGpsBlocked(boolean state) {
        if (mIsGpsBlocked != state) {
            initLocateMeFabIcon();
        }
        mIsGpsBlocked = state;
    }

    public void onClick(int viewId) {
        switch (viewId) {
            case R.id.floatingActionButtonLocateMe:
                mLocationManager.getLastLocation(new ILastLocationListener() {
                    @Override
                    public void onLastLocationSuccess(@Nullable Location location) {
                        if (location == null) {
                            return;
                        }
                        setGpsBlocked(false);
                        mView.moveCameraTo(location);
                    }

                    @Override
                    public void onLastLocationFailure(@NonNull String message) {
                        mView.showError(message);
                    }
                });
                if (mIsGpsBlocked) {
                    return;
                }
                mFollowLocation = true;
                mView.followLocationVisibility(View.VISIBLE);
                break;
            case R.id.mapOverlay:
                if (mIsGpsBlocked) {
                    return;
                }
                mFollowLocation = false;
                mView.followLocationVisibility(View.INVISIBLE);
                break;
            case R.id.floatingActionButtonLocateMeWifi:
                mLocationManager.getWifiBasedLocation(new ILastLocationListener() {
                    @Override
                    public void onLastLocationSuccess(@Nullable Location location) {
                        if (location == null) {
                            return;
                        }
                        mView.showWifiLocation(location);
                        mView.moveCameraTo(location);
                    }

                    @Override
                    public void onLastLocationFailure(@NonNull String message) {
                        mView.showError(message);
                    }
                });
                break;
            default:
                // do nothing here
        }
    }

    void onResume() {
        mLocationManager.subscribeToLocationChanges(this);
    }

    void onPause() {
        mLocationManager.unSubscribeToLocationChanges(this);
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        setGpsBlocked(false);
        mView.onLocationChanged(location);
        if (mFollowLocation) {
            mView.moveCameraTo(location);
        }
    }

    private void initLocateMeFabIcon() {
        if (!mLocationManager.hasLocationPermission()) {
            setGpsBlocked(true);
            mView.followLocationVisibility(View.GONE);
            return;
        }
        mLocationManager.deviceLocationSettingFulfilled(new ISettingsClientResultListener() {
            @Override
            public void onSuccess() {
                setGpsBlocked(false);
                mView.followLocationVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(@NonNull String message) {
                setGpsBlocked(true);
                mView.followLocationVisibility(View.GONE);
            }
        });
    }
}

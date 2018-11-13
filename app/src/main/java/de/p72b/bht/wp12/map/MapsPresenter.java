package de.p72b.bht.wp12.map;

import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

import de.p72b.bht.wp12.R;
import de.p72b.bht.wp12.location.ILastLocationListener;
import de.p72b.bht.wp12.location.ILocationUpdatesListener;
import de.p72b.bht.wp12.location.LocationManager;

class MapsPresenter implements ILastLocationListener, ILocationUpdatesListener {

    private LocationManager mLocationManager;
    private IMapsView mView;

    MapsPresenter(@NonNull final FragmentActivity fragmentActivity,
                  @NonNull final LocationManager locationManager) {
        mLocationManager = locationManager;
        mView = (IMapsView) fragmentActivity;
    }

    @Override
    public void onLastLocationSuccess(@Nullable Location location) {
        if (location == null) {
            // TODO trigger to request location updates.
            return;
        }
        mView.moveCameraTo(location);
    }

    @Override
    public void onLastLocationFailure(@NonNull String message) {
        mView.showError(message);
    }

    public void onClick(int viewId) {
        switch (viewId) {
            case R.id.floatingActionButtonLocateMe:
                mLocationManager.getLastLocation(this);
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

    }
}

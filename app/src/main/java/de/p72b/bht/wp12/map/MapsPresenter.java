package de.p72b.bht.wp12.map;

import android.location.Location;
import android.support.annotation.NonNull;

import de.p72b.bht.wp12.R;
import de.p72b.bht.wp12.location.ILastLocationListener;
import de.p72b.bht.wp12.location.LocationManager;

class MapsPresenter implements ILastLocationListener {
    private LocationManager mLocationManager;

    MapsPresenter(@NonNull final LocationManager locationManager) {
        mLocationManager = locationManager;
    }

    @Override
    public void onLastLocationSuccess(@NonNull Location location) {
        // TODO
    }

    @Override
    public void onLastLocationFailure(@NonNull String message) {
        // TODO
    }

    public void onClick(int viewId) {
        switch(viewId) {
            case R.id.floatingActionButtonLocateMe:
                mLocationManager.getLastLocation(this);
                break;
            default:
                // do nothing here
        }
    }
}

package de.p72b.bht.wp12.location;

import android.location.Location;
import android.support.annotation.NonNull;

public interface ILocationUpdatesListener {
    void onLocationChanged(@NonNull Location location);
}

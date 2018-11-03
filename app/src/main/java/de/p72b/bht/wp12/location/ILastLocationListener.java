package de.p72b.bht.wp12.location;

import android.location.Location;
import android.support.annotation.NonNull;

public interface ILastLocationListener {
    void onLastLocationSuccess(@NonNull final Location location);

    void onLastLocationFailure(@NonNull final String message);
}

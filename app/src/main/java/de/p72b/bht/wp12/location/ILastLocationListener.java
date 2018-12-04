package de.p72b.bht.wp12.location;

import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public interface ILastLocationListener {
    void onLastLocationSuccess(@Nullable final Location location);

    void onLastLocationFailure(@NonNull final String message);
}

package de.p72b.bht.wp12.map;

import android.location.Location;
import android.support.annotation.NonNull;

public interface IMapsView {
    void moveCameraTo(final Location location);

    void showError(final String message);

    void onLocationChanged(@NonNull final Location location);

    void followLocationVisibility(final int visibility);
}

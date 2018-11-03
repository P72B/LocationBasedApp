package de.p72b.bht.wp12.map;

import android.location.Location;

public interface IMapsView {
    void moveCameraTo(final Location location);

    void showError(final String message);
}

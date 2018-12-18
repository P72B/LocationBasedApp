package de.p72b.bht.wp12.map;

import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

public interface IMapsView {
    void moveCameraTo(@NonNull final Location location);

    void moveCameraTo(@NonNull final LatLng latLng);

    void showError(final String message);

    void onLocationChanged(@NonNull final Location location);

    void followLocationVisibility(final int visibility);

    void showWifiLocation(@NonNull final Location location);

    void showAddressLocation(@NonNull LatLng latLng);

    void showAddress(@NonNull final String title);

    void showDirection(@Nullable final PolylineOptions polylineOptions);
}

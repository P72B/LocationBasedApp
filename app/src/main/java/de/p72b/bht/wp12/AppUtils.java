package de.p72b.bht.wp12;

import android.location.Location;
import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLngBounds;

public class AppUtils {

    public static String getFormattedAddress(String formattedAddress) {
        String[] split = formattedAddress.split(",");
        if (split.length == 0) {
            return formattedAddress;
        }
        return split[0];
    }

    public static double getDiameter(@NonNull final LatLngBounds bounds) {
        final Location l1 = new Location("l1");
        l1.setLongitude(bounds.northeast.longitude);
        l1.setLatitude(bounds.northeast.latitude);
        final Location l2 = new Location("l2");
        l2.setLongitude(bounds.southwest.longitude);
        l2.setLatitude(bounds.southwest.latitude);
        return l1.distanceTo(l2);
    }
}

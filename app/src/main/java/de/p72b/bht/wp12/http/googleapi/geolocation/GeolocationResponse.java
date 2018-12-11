package de.p72b.bht.wp12.http.googleapi.geolocation;

import android.location.Location;
import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

public class GeolocationResponse {

    @SerializedName("location")
    private LocationResponse mLocation;

    @SerializedName("accuracy")
    private float mAccuracy;

    @Nullable
    public Location getLocation() {
        final Location location = new Location("wifi");
        location.setLatitude(mLocation.getLatitude());
        location.setLongitude(mLocation.getLongitude());
        location.setAccuracy(mAccuracy);
        return location;
    }

    public float getAccuracy() {
        return mAccuracy;
    }
}

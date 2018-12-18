package de.p72b.bht.wp12.http.googleapi.maps.geocode;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;

public class Geometry {
    @SerializedName("location")
    private Location mLocation;

    public Location getLocation() {
        return mLocation;
    }

    public static class Location {
        @SerializedName("lat")
        private String mLat;
        @SerializedName("lng")
        private String mLng;

        public LatLng getLatLng() {
            return new LatLng(Double.parseDouble(mLat), Double.parseDouble(mLng));
        }
    }
}

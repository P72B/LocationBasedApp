package de.p72b.bht.wp12.http.googleapi.maps.direction;

import com.google.gson.annotations.SerializedName;

public class LatLng {
    @SerializedName("lat")
    public double lat;

    @SerializedName("lng")
    public double lng;

    public com.google.android.gms.maps.model.LatLng getLatLng() {
        return new com.google.android.gms.maps.model.LatLng(lat, lng);
    }
}

package de.p72b.bht.wp12.http.googleapi.maps.direction;

import com.google.gson.annotations.SerializedName;

public class Bounds {
    @SerializedName("northeast")
    public LatLng northeast;

    @SerializedName("southwest")
    public LatLng southwest;
}

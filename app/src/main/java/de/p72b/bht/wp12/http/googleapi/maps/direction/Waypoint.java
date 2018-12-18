package de.p72b.bht.wp12.http.googleapi.maps.direction;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Waypoint {

    @SerializedName("geocoder_status")
    public String geocoderStatus;

    @SerializedName("place_id")
    public String placeId;

    @SerializedName("types")
    public List<String> types;
}

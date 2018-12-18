package de.p72b.bht.wp12.http.googleapi.maps.direction;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Leg {
    @SerializedName("distance")
    public Distance distance;

    @SerializedName("duration")
    public Duration duration;

    @SerializedName("end_address")
    public String endAddress;

    @SerializedName("end_location")
    public LatLng endLocation;

    @SerializedName("start_address")
    public String startAddress;

    @SerializedName("start_location")
    public LatLng startLocation;

    @SerializedName("steps")
    public List<Step> stepList;

    @SerializedName("traffic_speed_entry")
    public List<Object> trafficSpeedEntry;

    @SerializedName("via_waypoint")
    public List<Object> viaWaypoint;
}

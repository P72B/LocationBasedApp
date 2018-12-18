package de.p72b.bht.wp12.http.googleapi.maps.direction;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Route {
    @SerializedName("bounds")
    public Bounds bounds;

    @SerializedName("copyrights")
    public String copyrights;

    @SerializedName("legs")
    public List<Leg> legs;

    @SerializedName("overview_polyline")
    public Object overviewPolyline;

    @SerializedName("summary")
    public String summary;

    @SerializedName("warnings")
    public Object warnings;

    @SerializedName("waypoint_order")
    public List<Integer> waypointOrder;
}

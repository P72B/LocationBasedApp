package de.p72b.bht.wp12.http.googleapi.maps.direction;

import com.google.gson.annotations.SerializedName;

public class Step {
    @SerializedName("distance")
    public Distance distance;

    @SerializedName("duration")
    public Duration duration;

    @SerializedName("end_location")
    public LatLng endLocation;

    @SerializedName("start_location")
    public LatLng startLocation;

    @SerializedName("html_instructions")
    public String htmlInstructions;

    @SerializedName("travel_mode")
    public String travelMode;

    @SerializedName("polyline")
    public Polyline polyline;

    private class Polyline {
        @SerializedName("points")
        public String points;
    }
}

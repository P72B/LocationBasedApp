package de.p72b.bht.wp12.http.googleapi.maps.direction;

import android.support.annotation.Nullable;

import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DirectionsResponse {
    @SerializedName("geocoded_waypoints")
    public List<Waypoint> waypoint;

    @SerializedName("routes")
    public List<Route> routeList;

    @SerializedName("status")
    public String stauts;

    public PolylineOptions getPolyLineOptions() {
        final PolylineOptions polylineOptions = new PolylineOptions();
        if (routeList.size() == 0) {
            return polylineOptions;
        }
        final Route firstResult = routeList.get(0);
        for (Leg leg: firstResult.legs) {
            for (Step step: leg.stepList) {
                polylineOptions.add(step.startLocation.getLatLng());
                polylineOptions.add(step.endLocation.getLatLng());
            }
        }
        return polylineOptions;
    }

    @Nullable
    public String getDestinationAddress() {
        if (routeList.size() == 0) {
            return null;
        }
        final Route firstResult = routeList.get(0);
        final Leg lastLeg = firstResult.legs.get(firstResult.legs.size() - 1);
        return lastLeg.endAddress;
    }
}

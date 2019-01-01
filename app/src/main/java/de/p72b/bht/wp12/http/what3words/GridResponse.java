package de.p72b.bht.wp12.http.what3words;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import de.p72b.bht.wp12.http.googleapi.maps.direction.LatLng;

public class GridResponse {

    @SerializedName("lines")
    private List<Line> mLines;

    public List<Line> getLines() {
        return mLines;
    }

    public class Line {
        @SerializedName("start")
        private LatLng mStart;
        @SerializedName("end")
        private LatLng mEnd;

        public com.google.android.gms.maps.model.LatLng getStart() {
            return mStart.getLatLng();
        }

        public com.google.android.gms.maps.model.LatLng getEnd() {
            return mEnd.getLatLng();
        }
    }
}

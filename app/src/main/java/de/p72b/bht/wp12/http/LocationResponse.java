package de.p72b.bht.wp12.http;

import com.google.gson.annotations.SerializedName;

class LocationResponse {
    @SerializedName("lat")
    private float mLatitude;

    @SerializedName("lng")
    private float mLongitude;

    float getLatitude() {
        return mLatitude;
    }

    float getLongitude() {
        return mLongitude;
    }

}

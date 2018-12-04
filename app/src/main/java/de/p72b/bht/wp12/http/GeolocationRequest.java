package de.p72b.bht.wp12.http;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class GeolocationRequest {

    @SerializedName("homeMobileCountryCode")
    private int mHomeMobileCountryCode;

    @SerializedName("homeMobileNetworkCode")
    private int mHomeMobileNetworkCode;

    @Nullable
    @SerializedName("radioType")
    private String mRadioType;

    @Nullable
    @SerializedName("carrier")
    private String mCarrier;

    @Nullable
    @SerializedName("considerIp")
    private String mConsiderIp;

    @SerializedName("cellTowers")
    private List<Object> mCellTowers;

    @SerializedName("wifiAccessPoints")
    private List<WifiAccessPoint> mWifiAccessPoints;

    public GeolocationRequest(@Nullable List<WifiAccessPoint> wifiAccessPoints) {
        mWifiAccessPoints = wifiAccessPoints;
        mCellTowers = new ArrayList<>(); // TODO: implement as homework
    }
}

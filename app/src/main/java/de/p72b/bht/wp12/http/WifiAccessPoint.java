package de.p72b.bht.wp12.http;


import android.net.wifi.ScanResult;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class WifiAccessPoint {

    @SerializedName("macAddress")
    private String mMacAddress;

    @SerializedName("signalStrength")
    private int mSignalStrength;

    @SerializedName("age")
    private int mAge;

    @SerializedName("channel")
    private int mChannel;

    @SerializedName("signalToNoiseRatio")
    private int mSignalToNoiseRation;

    public WifiAccessPoint(@NonNull ScanResult scanResult) {
        mMacAddress = scanResult.BSSID;
        mSignalStrength = scanResult.level;
    }
}

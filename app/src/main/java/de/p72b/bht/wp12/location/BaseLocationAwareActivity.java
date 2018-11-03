package de.p72b.bht.wp12.location;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

@SuppressLint("Registered")
public class BaseLocationAwareActivity extends FragmentActivity {

    protected LocationManager mLocationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocationManager = new LocationManager(this);
    }
}

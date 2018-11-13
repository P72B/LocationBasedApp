package de.p72b.bht.wp12.map;

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import de.p72b.bht.wp12.R;
import de.p72b.bht.wp12.location.BaseLocationAwareActivity;

public class MapsActivity extends BaseLocationAwareActivity implements IMapsView,
        OnMapReadyCallback, View.OnClickListener, LocationSource {

    private GoogleMap mMap;
    private MapsPresenter mPresenter;
    private View mRootView;
    private OnLocationChangedListener mMapLocationListener = null;
    private boolean mFirstLocationUpdate = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        mPresenter = new MapsPresenter(this, mLocationManager);

        mRootView = findViewById(android.R.id.content);
        findViewById(R.id.floatingActionButtonLocateMe).setOnClickListener(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        mMap.setLocationSource(this);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.onResume();
    }

    @Override
    protected void onPause() {
        mPresenter.onPause();
        super.onPause();
    }

    @Override
    public void onClick(View view) {
        mPresenter.onClick(view.getId());
    }

    @Override
    public void moveCameraTo(@NonNull final Location location) {
        if (mMap == null) {
            return;
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),
                location.getLongitude()), 17));
    }

    @Override
    public void showError(@NonNull String message) {
        Snackbar.make(mRootView, message, Snackbar.LENGTH_LONG).show();
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onLocationChanged(@NonNull Location location) {
        if (mFirstLocationUpdate) {
            if (mLocationManager.hasLocationPermission()) {
                mMap.setMyLocationEnabled(true);
            }
            mFirstLocationUpdate = false;
        }

        if (mMapLocationListener != null) {
            mMapLocationListener.onLocationChanged(location);
        }
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mMapLocationListener = onLocationChangedListener;
    }

    @Override
    public void deactivate() {
        mMapLocationListener = null;
    }
}

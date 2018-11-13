package de.p72b.bht.wp12.map;

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.MotionEvent;
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
import de.p72b.bht.wp12.location.ISettingsClientResultListener;

public class MapsActivity extends BaseLocationAwareActivity implements IMapsView,
        OnMapReadyCallback, View.OnClickListener, LocationSource {

    private GoogleMap mMap;
    private MapsPresenter mPresenter;
    private View mRootView;
    private OnLocationChangedListener mMapLocationListener = null;
    private boolean mFirstLocationUpdate = true;
    private FloatingActionButton mLocateMeFab;
    private int mLastFollowLocationVisibility;

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

        mRootView = findViewById(android.R.id.content);
        mLocateMeFab = findViewById(R.id.floatingActionButtonLocateMe);
        mLocateMeFab.setOnClickListener(this);

        findViewById(R.id.mapOverlay).setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                mPresenter.onClick(view.getId());
                return false;
            }
        });

        mPresenter = new MapsPresenter(this, mLocationManager);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng berlin = new LatLng(52.45, 13.64);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(berlin));

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
    public void followLocationVisibility(int visibility) {
        if (mLastFollowLocationVisibility == visibility) {
            return;
        }
        mLastFollowLocationVisibility = visibility;

        if (View.VISIBLE == visibility) {
            mLocateMeFab.setImageResource(R.drawable.ic_gps_fixed_black_24dp);
        } else if(View.INVISIBLE == visibility) {
            mLocateMeFab.setImageResource(R.drawable.ic_gps_not_fixed_black_24dp);
        } else {
            mLocateMeFab.setImageResource(R.drawable.ic_gps_off_black_24dp);
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

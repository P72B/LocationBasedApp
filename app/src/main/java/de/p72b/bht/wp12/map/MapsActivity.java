package de.p72b.bht.wp12.map;

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.MotionEvent;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

import de.p72b.bht.wp12.R;
import de.p72b.bht.wp12.Service.AppServices;
import de.p72b.bht.wp12.http.IWebService;
import de.p72b.bht.wp12.http.what3words.GridResponse;
import de.p72b.bht.wp12.location.BaseLocationAwareActivity;

public class MapsActivity extends BaseLocationAwareActivity implements IMapsView,
        OnMapReadyCallback, View.OnClickListener, LocationSource, GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;
    private MapsPresenter mPresenter;
    private View mRootView;
    private OnLocationChangedListener mMapLocationListener = null;
    private boolean mFirstLocationUpdate = true;
    private FloatingActionButton mLocateMeFab;
    private FloatingActionButton mLocateMeFabWifi;
    private int mLastFollowLocationVisibility;
    private Circle mWifiCircle;
    private Marker mAddressMarker;
    private Polyline mDirection;
    private List<Polyline> mGrid = new ArrayList<>();

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
        mLocateMeFabWifi = findViewById(R.id.floatingActionButtonLocateMeWifi);
        mLocateMeFabWifi.setOnClickListener(this);

        findViewById(R.id.mapOverlay).setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                mPresenter.onClick(view.getId());
                return false;
            }
        });

        IWebService webService = AppServices.getService(AppServices.WEB);
        mPresenter = new MapsPresenter(this, mLocationManager, webService);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng berlin = new LatLng(52.45, 13.64);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(berlin));
        mMap.setOnMapLongClickListener(this);

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
    public void moveCameraTo(@NonNull final LatLng latLng) {
        if (mMap == null) {
            return;
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
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
    public void showWifiLocation(@NonNull Location location) {
        if (mMap == null) {
            return;
        }
        final CircleOptions circleOptions = new CircleOptions()
                .center(new LatLng(location.getLatitude(), location.getLongitude()))
                .strokeWidth(2)
                .strokeColor(getApplicationContext().getColor(R.color.colorPrimary))
                .fillColor(getApplicationContext().getColor(R.color.fillWifiCircle))
                .radius(location.getAccuracy());
        if (mWifiCircle != null) {
            mWifiCircle.remove();
        }
        mWifiCircle = mMap.addCircle(circleOptions);
    }

    @Override
    public void showAddressLocation(@NonNull LatLng latLng) {
        updateAddressMarker(latLng, null);
    }

    @Override
    public void showAddress(@NonNull final String title) {
        updateAddressMarker(mAddressMarker.getPosition(), title);
    }

    @Override
    public void showDirection(@Nullable PolylineOptions polylineOptions) {
        if (mMap == null) {
            return;
        }
        if (mDirection != null) {
            mDirection.remove();
        }
        if (polylineOptions != null) {
            mDirection = mMap.addPolyline(polylineOptions);
        }
    }

    private void updateAddressMarker(@NonNull final LatLng latLng, @Nullable final String title) {
        if (mMap == null) {
            return;
        }
        final MarkerOptions markerOptions = new MarkerOptions()
                .title(title)
                .position(latLng);
        if (mAddressMarker != null) {
            mAddressMarker.remove();
        }
        mAddressMarker = mMap.addMarker(markerOptions);
        if (title != null) {
            mAddressMarker.showInfoWindow();
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

    @Override
    public void onMapLongClick(LatLng latLng) {
        mPresenter.onMapLongClick(latLng);
    }

    @Override
    @Nullable
    public LatLngBounds getVisibleViewport() {
        if (mMap == null) {
            return null;
        }
        return mMap.getProjection().getVisibleRegion().latLngBounds;
    }

    @Override
    public void showGrid(@NonNull final List<PolylineOptions> polylines) {
        for (int i = 0; i < mGrid.size(); i++) {
            mGrid.get(i).remove();
        }
        mGrid.clear();

        for (PolylineOptions polylineOptions: polylines) {
            final Polyline polyline = mMap.addPolyline(polylineOptions);
            mGrid.add(polyline);
        }
    }
}

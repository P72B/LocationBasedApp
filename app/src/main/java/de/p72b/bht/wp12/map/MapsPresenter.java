package de.p72b.bht.wp12.map;

import android.content.Context;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

import de.p72b.bht.wp12.AppUtils;
import de.p72b.bht.wp12.R;
import de.p72b.bht.wp12.http.IWebService;
import de.p72b.bht.wp12.http.googleapi.maps.direction.DirectionsResponse;
import de.p72b.bht.wp12.http.googleapi.maps.geocode.AddressResponse;
import de.p72b.bht.wp12.http.what3words.GridResponse;
import de.p72b.bht.wp12.http.what3words.ReverseResponse;
import de.p72b.bht.wp12.location.ILastLocationListener;
import de.p72b.bht.wp12.location.ILocationUpdatesListener;
import de.p72b.bht.wp12.location.ISettingsClientResultListener;
import de.p72b.bht.wp12.location.LocationManager;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

class MapsPresenter implements ILocationUpdatesListener {

    private LocationManager mLocationManager;
    private IMapsView mView;
    private boolean mFollowLocation = false;
    private boolean mIsGpsBlocked = true;
    private Disposable mGeocodeDisposable;
    private Disposable mReverseGeocodeDisposable;
    private Disposable mDirectionsDisposable;
    private Disposable mGridDisposable;
    private Disposable mReverseDisposable;
    private IWebService mWebService;
    private Location mLastKnownLocation = null;
    private Context mContext;

    MapsPresenter(@NonNull final FragmentActivity fragmentActivity,
                  @NonNull final LocationManager locationManager,
                  @NonNull final IWebService webService) {
        mLocationManager = locationManager;
        mWebService = webService;
        mContext = fragmentActivity.getApplicationContext();
        mView = (IMapsView) fragmentActivity;
        initLocateMeFabIcon();
    }

    private void setGpsBlocked(boolean state) {
        if (mIsGpsBlocked != state) {
            initLocateMeFabIcon();
        }
        mIsGpsBlocked = state;
    }

    public void onClick(int viewId) {
        switch (viewId) {
            case R.id.floatingActionButtonLocateMe:
                mLocationManager.getLastLocation(new ILastLocationListener() {
                    @Override
                    public void onLastLocationSuccess(@Nullable Location location) {
                        if (location == null) {
                            return;
                        }
                        mLastKnownLocation = location;
                        setGpsBlocked(false);
                        mView.moveCameraTo(location);
                    }

                    @Override
                    public void onLastLocationFailure(@NonNull String message) {
                        mView.showError(message);
                    }
                });
                if (mIsGpsBlocked) {
                    return;
                }
                mFollowLocation = true;
                mView.followLocationVisibility(View.VISIBLE);
                break;
            case R.id.mapOverlay:
                if (mIsGpsBlocked) {
                    return;
                }
                mFollowLocation = false;
                mView.followLocationVisibility(View.INVISIBLE);
                break;
            case R.id.floatingActionButtonLocateMeWifi:
                mLocationManager.getWifiBasedLocation(new ILastLocationListener() {
                    @Override
                    public void onLastLocationSuccess(@Nullable Location location) {
                        if (location == null) {
                            return;
                        }
                        mView.showWifiLocation(location);
                        mView.moveCameraTo(location);
                    }

                    @Override
                    public void onLastLocationFailure(@NonNull String message) {
                        mView.showError(message);
                    }
                });
                break;
            default:
                // do nothing here
        }
    }

    void onResume() {
        mLocationManager.subscribeToLocationChanges(this);
    }

    void onPause() {
        mLocationManager.unSubscribeToLocationChanges(this);
        shutDownDisposable(mDirectionsDisposable);
        shutDownDisposable(mReverseGeocodeDisposable);
        shutDownDisposable(mGeocodeDisposable);
        shutDownDisposable(mGridDisposable);
        shutDownDisposable(mReverseDisposable);
    }

    void onMapLongClick(@NonNull final LatLng latLng) {
        if (mLastKnownLocation == null) {
            mView.showDirection(null);
            reverseGeoCode(latLng);
            return;
        }
        final Location target = new Location("test");
        target.setLatitude(latLng.latitude);
        target.setLongitude(latLng.longitude);
        if (mLastKnownLocation.distanceTo(target) > 10_000) {
            mView.showDirection(null);
            reverseGeoCode(latLng);
            return;
        }
        final LatLngBounds bounds = mView.getVisibleViewport();
        if (bounds == null) {
            calculateDirection(new LatLng(mLastKnownLocation.getLatitude(),
                    mLastKnownLocation.getLongitude()), latLng);
            return;
        }

        final double diameter = AppUtils.getDiameter(bounds);
        if (diameter > 2_000) {
            calculateDirection(new LatLng(mLastKnownLocation.getLatitude(),
                    mLastKnownLocation.getLongitude()), latLng);
        } else {
            reverse(latLng);
            grid(bounds);
        }
    }

    private void calculateDirection(@NonNull final LatLng origin, @NonNull final LatLng destination) {
        mView.showAddressLocation(destination);
        shutDownDisposable(mDirectionsDisposable);
        mWebService.calculateDirections(origin, destination)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<DirectionsResponse>() {
                    @Override
                    public void onSubscribe(Disposable disposable) {
                        mDirectionsDisposable = disposable;
                    }

                    @Override
                    public void onNext(DirectionsResponse directionsResponse) {
                        if (directionsResponse == null) {
                            return;
                        }
                        final PolylineOptions polylineOptions = directionsResponse.getPolyLineOptions();
                        final String address = directionsResponse.getDestinationAddress();
                        if (address != null) {
                            mView.showAddress(address);
                        } else {
                            mView.showError("Address could not be resolved by google.");
                        }
                        mView.showDirection(polylineOptions);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.showError("Direction couldn't be calculated.");
                    }

                    @Override
                    public void onComplete() {
                        // do noting here so far
                    }
                });
    }

    private void reverseGeoCode(LatLng latLng) {
        mView.showAddressLocation(latLng);
        shutDownDisposable(mReverseGeocodeDisposable);
        mWebService.reverseGeoCoding(latLng)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<AddressResponse>() {
                    @Override
                    public void onSubscribe(Disposable disposable) {
                        mReverseGeocodeDisposable = disposable;
                    }

                    @Override
                    public void onNext(AddressResponse addressResponse) {
                        if (!addressResponse.getResults().isEmpty()) {
                            String address = addressResponse.getResults().get(0).getFormattedAddress();
                            mView.showAddress(address);
                        } else {
                            mView.showError("Address could not be resolved by google.");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.showError("No address found some error occurred.");
                    }

                    @Override
                    public void onComplete() {
                        // nothing to do here
                    }
                });
    }

    private void geocode(final String address) {
        shutDownDisposable(mGeocodeDisposable);
        mWebService.geoCoding(address)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<AddressResponse>() {

                    @Override
                    public void onSubscribe(Disposable disposable) {
                        mGeocodeDisposable = disposable;
                    }

                    @Override
                    public void onNext(AddressResponse addressResponse) {
                        final List<AddressResponse.Result> resultList = addressResponse.getResults();
                        if (resultList.size() > 0) {
                            final LatLng resultLatLng = resultList.get(0).getLatLng();
                            mView.showError(resultLatLng.toString());
                            String address = addressResponse.getResults().get(0).getFormattedAddress();
                            mView.showAddressLocation(resultLatLng);
                            mView.showAddress(address);
                            mView.moveCameraTo(resultLatLng);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void grid(@NonNull final LatLngBounds bounds) {
        shutDownDisposable(mGridDisposable);
        mWebService.grid(bounds)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GridResponse>() {

                    @Override
                    public void onSubscribe(Disposable disposable) {
                        mGridDisposable = disposable;
                    }

                    @Override
                    public void onNext(GridResponse response) {
                        final List<PolylineOptions> polylineOptionsList = new ArrayList<>();
                        for (GridResponse.Line line: response.getLines()) {
                            final PolylineOptions polylineOptions = new PolylineOptions();
                            polylineOptions.color(ContextCompat.getColor(mContext, R.color.grid));
                            polylineOptions.width(0.8f);
                            polylineOptions.add(line.getStart());
                            polylineOptions.add(line.getEnd());
                            polylineOptionsList.add(polylineOptions);
                        }
                        mView.showGrid(polylineOptionsList);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.showError("No grid found some error occurred.");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    private void reverse(@NonNull final LatLng latLng) {
        mView.showAddressLocation(latLng);
        shutDownDisposable(mReverseDisposable);
        mWebService.reverse(latLng)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ReverseResponse>() {

                    @Override
                    public void onSubscribe(Disposable disposable) {
                        mReverseDisposable = disposable;
                    }

                    @Override
                    public void onNext(ReverseResponse response) {
                        final String words = response.getWords();
                        if (words != null) {
                            mView.showAddress(words);
                        } else {
                            mView.showError("No what3words address could not resolved.");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.showError("No what3words address found some error occurred.");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        mLastKnownLocation = location;
        setGpsBlocked(false);
        mView.onLocationChanged(location);
        if (mFollowLocation) {
            mView.moveCameraTo(location);
        }
    }

    private void initLocateMeFabIcon() {
        if (!mLocationManager.hasLocationPermission()) {
            setGpsBlocked(true);
            mView.followLocationVisibility(View.GONE);
            return;
        }
        mLocationManager.deviceLocationSettingFulfilled(new ISettingsClientResultListener() {
            @Override
            public void onSuccess() {
                setGpsBlocked(false);
                mView.followLocationVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(@NonNull String message) {
                setGpsBlocked(true);
                mView.followLocationVisibility(View.GONE);
            }
        });
    }

    private void shutDownDisposable(@Nullable final Disposable disposable) {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }
}

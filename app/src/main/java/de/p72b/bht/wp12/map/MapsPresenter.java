package de.p72b.bht.wp12.map;

import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.google.android.gms.maps.model.LatLng;

import de.p72b.bht.wp12.R;
import de.p72b.bht.wp12.http.IWebService;
import de.p72b.bht.wp12.http.googleapi.geocode.AddressResponse;
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
    private IWebService mWebService;

    MapsPresenter(@NonNull final FragmentActivity fragmentActivity,
                  @NonNull final LocationManager locationManager,
                  @NonNull final IWebService webService) {
        mLocationManager = locationManager;
        mWebService = webService;
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
        shutDownDisposables();
    }

    void onMapLongClick(@NonNull final LatLng latLng) {
        mView.showAddressLocation(latLng);
        shutDownDisposables();
        mWebService.reverseGeoCoding(latLng)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<AddressResponse>() {
                    @Override
                    public void onSubscribe(Disposable disposable) {
                        mGeocodeDisposable = disposable;
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

    @Override
    public void onLocationChanged(@NonNull Location location) {
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

    private void shutDownDisposables() {
        if (mGeocodeDisposable != null && !mGeocodeDisposable.isDisposed()) {
            mGeocodeDisposable.dispose();
        }
    }
}

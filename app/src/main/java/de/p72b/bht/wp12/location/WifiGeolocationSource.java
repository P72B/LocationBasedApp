package de.p72b.bht.wp12.location;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import de.p72b.bht.wp12.App;
import de.p72b.bht.wp12.http.googleapi.geolocation.GeolocationRequest;
import de.p72b.bht.wp12.http.googleapi.geolocation.GeolocationResponse;
import de.p72b.bht.wp12.http.IWebService;
import de.p72b.bht.wp12.http.googleapi.geolocation.WifiAccessPoint;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

class WifiGeolocationSource {

    private final WifiManager mWifiManager;
    private final IWebService mWebService;
    private ILastLocationListener mListener;
    private PermissionManager mPermissionManger;
    private Disposable mGeolocationDisposable;

    WifiGeolocationSource(@NonNull IWebService webService,
                          @NonNull final PermissionManager permissionManager) {
        mWebService = webService;
        mPermissionManger = permissionManager;
        mWifiManager = (WifiManager) App.getInstance().getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);

        final IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        App.getInstance().getApplicationContext()
                .registerReceiver(new WifiScanResultAvailableReceiver(), filter);
    }

    void scanForWifi(@NonNull ILastLocationListener listener) {
        mListener = listener;
        if (!mPermissionManger.hasPermissionIfNotRequest(Manifest.permission.ACCESS_FINE_LOCATION)) {
            listener.onLastLocationFailure("Location permission missing");
            return; // early return here
        }

        if (mWifiManager == null) {
            listener.onLastLocationFailure("Wifi manager missing");
            return;
        }
        mWifiManager.startScan();
    }

    private void geolocate(@Nullable List<WifiAccessPoint> wifiAccessPointList) {
        final GeolocationRequest geolocationRequest = new GeolocationRequest(wifiAccessPointList);
        if (mGeolocationDisposable != null && !mGeolocationDisposable.isDisposed()) {
            mGeolocationDisposable.dispose();
        }
        final Location[] location = {null};
        mWebService.geolocate(geolocationRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GeolocationResponse>() {
                    @Override
                    public void onSubscribe(Disposable disposable) {
                        mGeolocationDisposable = disposable;
                    }

                    @Override
                    public void onNext(GeolocationResponse geolocationResponse) {
                        location[0] = geolocationResponse.getLocation();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mListener.onLastLocationFailure("Http error");
                    }

                    @Override
                    public void onComplete() {
                        if (location[0] == null) {
                            mListener.onLastLocationFailure("Location is null");
                            return;
                        }
                        mListener.onLastLocationSuccess(location[0]);
                    }
                });
    }

    private class WifiScanResultAvailableReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mWifiManager == null || mListener == null) {
                return;
            }
            final List<ScanResult> scanResultList = mWifiManager.getScanResults();
            if (scanResultList.size() == 0) {
                return;
            }

            final List<WifiAccessPoint> wifiAccessPoints = new ArrayList<>(scanResultList.size());
            for (ScanResult scanResult : scanResultList) {
                wifiAccessPoints.add(new WifiAccessPoint(scanResult));
            }
            geolocate(wifiAccessPoints);
        }
    }
}

package de.p72b.bht.wp12.http;

import android.support.annotation.Nullable;

import io.reactivex.Observable;

public interface IWebService {

    Observable<GeolocationResponse> geolocate(@Nullable GeolocationRequest geolocationRequest);
}

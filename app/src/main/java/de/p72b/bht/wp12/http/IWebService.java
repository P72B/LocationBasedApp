package de.p72b.bht.wp12.http;

import android.support.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;

import de.p72b.bht.wp12.http.googleapi.geocode.AddressResponse;
import de.p72b.bht.wp12.http.googleapi.geolocation.GeolocationRequest;
import de.p72b.bht.wp12.http.googleapi.geolocation.GeolocationResponse;
import io.reactivex.Observable;

public interface IWebService {

    Observable<GeolocationResponse> geolocate(@Nullable GeolocationRequest geolocationRequest);

    Observable<AddressResponse> reverseGeoCoding(LatLng latLng);

    Observable<AddressResponse> geoCoding(String address);
}

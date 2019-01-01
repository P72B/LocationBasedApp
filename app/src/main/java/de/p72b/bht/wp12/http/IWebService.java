package de.p72b.bht.wp12.http;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import de.p72b.bht.wp12.http.googleapi.maps.geocode.AddressResponse;
import de.p72b.bht.wp12.http.googleapi.geolocation.GeolocationRequest;
import de.p72b.bht.wp12.http.googleapi.geolocation.GeolocationResponse;
import de.p72b.bht.wp12.http.googleapi.maps.direction.DirectionsResponse;
import de.p72b.bht.wp12.http.what3words.GridResponse;
import de.p72b.bht.wp12.http.what3words.ReverseResponse;
import io.reactivex.Observable;

public interface IWebService {

    Observable<GeolocationResponse> geolocate(@Nullable GeolocationRequest geolocationRequest);

    Observable<AddressResponse> reverseGeoCoding(@NonNull final LatLng latLng);

    Observable<AddressResponse> geoCoding(@NonNull final String address);

    Observable<DirectionsResponse> calculateDirections(@NonNull final LatLng origin,
                                                       @NonNull final LatLng destination);

    Observable<GridResponse> grid(@NonNull final LatLngBounds bounds);

    Observable<ReverseResponse> reverse(@NonNull final LatLng latLng);
}

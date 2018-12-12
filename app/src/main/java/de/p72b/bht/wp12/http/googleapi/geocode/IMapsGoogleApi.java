package de.p72b.bht.wp12.http.googleapi.geocode;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface IMapsGoogleApi {

    String ENDPOINT_MAPS_API_GEOCODE = "/maps/api/geocode/json";

    @GET(ENDPOINT_MAPS_API_GEOCODE)
    Observable<AddressResponse> reverseGeocodeLocation(@Query("key") String key,
                                                       @Query("latlng") String latLng,
                                                       @Query("language") String language);

    @GET(ENDPOINT_MAPS_API_GEOCODE)
    Observable<AddressResponse> geocodeLocation(@Query("key") String key,
                                                @Query("address") String address);

}

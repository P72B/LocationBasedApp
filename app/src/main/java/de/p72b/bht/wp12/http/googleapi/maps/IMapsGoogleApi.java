package de.p72b.bht.wp12.http.googleapi.maps;

import de.p72b.bht.wp12.http.googleapi.maps.direction.DirectionsResponse;
import de.p72b.bht.wp12.http.googleapi.maps.geocode.AddressResponse;
import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface IMapsGoogleApi {

    String ENDPOINT_MAPS_API_GEOCODE = "/maps/api/geocode/json";
    String ENDPOINT_MAPS_API_DIRECTIONS = "/maps/api/directions/json";

    @GET(ENDPOINT_MAPS_API_GEOCODE)
    Observable<AddressResponse> reverseGeocodeLocation(@Query("key") String key,
                                                       @Query("latlng") String latLng,
                                                       @Query("language") String language);

    @GET(ENDPOINT_MAPS_API_GEOCODE)
    Observable<AddressResponse> geocodeLocation(@Query("key") String key,
                                                @Query("address") String address);

    @GET(ENDPOINT_MAPS_API_DIRECTIONS)
    Observable<DirectionsResponse> calculateDirections(@Query("key") String key,
                                                       @Query("origin") String origin,
                                                       @Query("destination") String destination);

}

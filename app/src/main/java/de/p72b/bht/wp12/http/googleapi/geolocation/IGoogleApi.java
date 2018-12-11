package de.p72b.bht.wp12.http.googleapi.geolocation;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;


public interface IGoogleApi {

    @POST("/geolocation/v1/geolocate")
    Observable<GeolocationResponse> geolocate(@Query("key") String key,
                                              @Body GeolocationRequest geolocationRequest);
}

package de.p72b.bht.wp12.http;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.Locale;

import de.p72b.bht.wp12.BuildConfig;
import de.p72b.bht.wp12.http.googleapi.maps.geocode.AddressResponse;
import de.p72b.bht.wp12.http.googleapi.maps.direction.DirectionsResponse;
import de.p72b.bht.wp12.http.googleapi.maps.IMapsGoogleApi;
import de.p72b.bht.wp12.http.googleapi.geolocation.GeolocationRequest;
import de.p72b.bht.wp12.http.googleapi.geolocation.GeolocationResponse;
import de.p72b.bht.wp12.http.googleapi.geolocation.IGoogleApi;
import de.p72b.bht.wp12.http.what3words.ApiHeaderInterceptor;
import de.p72b.bht.wp12.http.what3words.GridResponse;
import de.p72b.bht.wp12.http.what3words.IWhat3WordsApi;
import de.p72b.bht.wp12.http.what3words.ReverseResponse;
import io.reactivex.Observable;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class WebService implements IWebService {

    private IGoogleApi mProxyGoogleApiService;
    private IWhat3WordsApi mProxyWhat3WordsService;
    private IMapsGoogleApi mProxyMapsGoogleApiService;
    private final String mGoogleApiKey = "KEY_HERE";
    private final String mWhat3WordsApiKey = "KEY_HERE";

    public WebService() {

    }

    private IWhat3WordsApi getWhat3WordsApi() {
        if (mProxyWhat3WordsService != null) {
            return mProxyWhat3WordsService;
        }
        OkHttpClient.Builder client = new OkHttpClient.Builder();
        client.addInterceptor(new ApiHeaderInterceptor(mWhat3WordsApiKey));

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.WHAT_3_WORDS_API_BACKEND)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(client.build())
                .build();
        mProxyWhat3WordsService = retrofit.create(IWhat3WordsApi.class);
        return mProxyWhat3WordsService;
    }

    private IGoogleApi getGoogleApi() {
        if (mProxyGoogleApiService != null) {
            return mProxyGoogleApiService;
        }
        OkHttpClient.Builder client = new OkHttpClient.Builder();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.GOOGLE_API_BACKEND)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(client.build())
                .build();
        mProxyGoogleApiService = retrofit.create(IGoogleApi.class);
        return mProxyGoogleApiService;
    }

    private IMapsGoogleApi getMapsGoogleApi() {
        if (mProxyMapsGoogleApiService != null) {
            return mProxyMapsGoogleApiService;
        }
        OkHttpClient.Builder client = new OkHttpClient.Builder();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.MAPS_GOOGLE_API_BACKEND)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(client.build())
                .build();
        mProxyMapsGoogleApiService = retrofit.create(IMapsGoogleApi.class);
        return mProxyMapsGoogleApiService;
    }

    @Override
    public Observable<GeolocationResponse> geolocate(@Nullable GeolocationRequest geolocationRequest) {
        return getGoogleApi().geolocate(mGoogleApiKey, geolocationRequest);
    }

    @Override
    public Observable<AddressResponse> reverseGeoCoding(@NonNull final LatLng latLng) {
        final String locale = Locale.getDefault().getLanguage();
        return getMapsGoogleApi().reverseGeocodeLocation(mGoogleApiKey,
                getGoogleApiRepresentation(latLng), locale);
    }

    @Override
    public Observable<AddressResponse> geoCoding(@NonNull final String address) {
        return getMapsGoogleApi().geocodeLocation(mGoogleApiKey,address);
    }

    @Override
    public Observable<DirectionsResponse> calculateDirections(@NonNull LatLng origin,
                                                              @NonNull LatLng destination) {
        return getMapsGoogleApi().calculateDirections(mGoogleApiKey,
                getGoogleApiRepresentation(origin),
                getGoogleApiRepresentation(destination));
    }

    @Override
    public Observable<GridResponse> grid(@NonNull final LatLngBounds bbox) {
        return getWhat3WordsApi().grid("json",
                getWhat3WordsApiRepresentation(bbox));
    }


    @Override
    public Observable<ReverseResponse> reverse(@NonNull final LatLng latLng) {
        return getWhat3WordsApi().reverse("json",
                getGoogleApiRepresentation(latLng));
    }

    @NonNull
    private String getGoogleApiRepresentation(@NonNull final LatLng latLng) {
        return latLng.latitude + "," + latLng.longitude;
    }

    @NonNull
    private String getWhat3WordsApiRepresentation(@NonNull final LatLngBounds bounds) {
        return bounds.northeast.latitude + "," + bounds.northeast.longitude + ","
                + bounds.southwest.latitude + "," + bounds.southwest.longitude;
    }
}

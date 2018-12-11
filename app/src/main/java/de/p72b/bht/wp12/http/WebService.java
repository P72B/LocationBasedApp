package de.p72b.bht.wp12.http;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;

import java.util.Locale;

import de.p72b.bht.wp12.BuildConfig;
import de.p72b.bht.wp12.http.googleapi.geocode.AddressResponse;
import de.p72b.bht.wp12.http.googleapi.geocode.IMapsGoogleApi;
import de.p72b.bht.wp12.http.googleapi.geolocation.GeolocationRequest;
import de.p72b.bht.wp12.http.googleapi.geolocation.GeolocationResponse;
import de.p72b.bht.wp12.http.googleapi.geolocation.IGoogleApi;
import io.reactivex.Observable;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class WebService implements IWebService {

    private IGoogleApi mProxyGoogleApiService;
    private IMapsGoogleApi mProxyMapsGoogleApiService;
    private final String mGoogleApiKey = "KEY_HERE";

    public WebService() {

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
                latLng.latitude + "," + latLng.longitude, locale);
    }
}

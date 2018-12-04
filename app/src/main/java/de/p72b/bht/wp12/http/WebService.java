package de.p72b.bht.wp12.http;

import android.support.annotation.Nullable;

import de.p72b.bht.wp12.BuildConfig;
import io.reactivex.Observable;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class WebService implements IWebService {

    private IGoogleApi mProxyGoogleApiService;

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

    @Override
    public Observable<GeolocationResponse> geolocate(@Nullable GeolocationRequest geolocationRequest) {
        return getGoogleApi().geolocate("KEY_HERE", geolocationRequest);
    }
}

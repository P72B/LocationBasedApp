package de.p72b.bht.wp12.http.what3words;

import android.support.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;


public class ApiHeaderInterceptor
        implements Interceptor {

    private final String mToken;

    public ApiHeaderInterceptor(@NonNull final String key) {
        mToken = key;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        final Request.Builder requestBuilder = request.newBuilder();
        requestBuilder.addHeader("X-Api-Key", mToken);

        return chain.proceed(requestBuilder.build());
    }
}

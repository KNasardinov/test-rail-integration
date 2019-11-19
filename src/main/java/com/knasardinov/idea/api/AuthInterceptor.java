package com.knasardinov.idea.api;

import okhttp3.*;
import java.io.IOException;

public final class AuthInterceptor implements Interceptor {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String APPLICATION_JSON = "application/json";
    private final String credentials;

    AuthInterceptor(String userName, String password) {
        this.credentials = Credentials.basic(userName, password);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        String url = original.url().toString().replaceAll("%3F", "?");
        Request.Builder requestBuilder = original.newBuilder()
                .header(AUTHORIZATION_HEADER, credentials)
                .header(CONTENT_TYPE, APPLICATION_JSON)
                .url(url);

        Request request = requestBuilder.build();
        return chain.proceed(request);
    }

}
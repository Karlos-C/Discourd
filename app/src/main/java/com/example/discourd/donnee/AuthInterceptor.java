package com.example.discourd.donnee;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {

    private String token;
    private String userId;

    public AuthInterceptor(String token, String userId) {
        this.token = token;
        this.userId = userId;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        Request newRequest = originalRequest.newBuilder()
                .header("Authorization", "Bearer " + token)
                .header("userId", userId)
                .build();

        // Debug : log headers
        System.out.println("Authorization: Bearer " + token);
        System.out.println("userId: " + userId);

        return chain.proceed(newRequest);
    }

}


package com.example.spacr.NASAapi;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiDisplayClient {

    public static final String BASE_URL = "https://api.nasa.gov/";
    public static Retrofit retrofit;

    public static Retrofit getApiDisplayClient() {

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

}

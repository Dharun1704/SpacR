package com.example.spacr.NASAapi;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiSearchClient {

    public static final String BASE_URL = "https://images-api.nasa.gov/";
    public static Retrofit retrofit;

    public static Retrofit getApiSearchClient() {

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}

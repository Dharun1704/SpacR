package com.example.spacr.NASAapi;

import com.example.spacr.Models.APODResults;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiDisplayInterface {

    @GET("planetary/apod")
    Call<APODResults> getAPOD(
            @Query("date") String date,
            @Query("hd") boolean hd,
            @Query("api_key") String api_key
    );
}

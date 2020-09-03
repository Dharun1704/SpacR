package com.example.spacr.NASAapi;

import com.example.spacr.Models.SearchResult;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiSearchInterface {

    @GET("search")
    Call<SearchResult> getSearchResults(
            @Query("q") String search
    );
}

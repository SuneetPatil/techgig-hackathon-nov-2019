package com.myapp.rest;

import com.myapp.model.LangTranslator;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;


public interface ApiInterface {

    @GET("Translate")
    Call<LangTranslator> getData(@Query("text") String text, @Query("from") String from, @Query("to") String to);

}
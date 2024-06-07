package com.example.myapplication.network;

import com.example.myapplication.model.QuoteResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ZenQuotesApiService {
    @GET("/api/today")
    Call<List<QuoteResponse>> getDailyQuote();
}
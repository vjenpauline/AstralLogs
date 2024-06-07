package com.example.myapplication.network;

import com.example.myapplication.model.QuoteResponse; // imports QuoteResponse
import java.util.List;
import retrofit2.Call; // imports Call from Retrofit library
import retrofit2.http.GET; // imports GET annotation from Retrofit library.

public interface ZenQuotesApiService {
    @GET("/api/today")
    Call<List<QuoteResponse>> getDailyQuote();
}
package com.zybooks.androidassignments.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object CurrencyService {
    private const val BASE_URL = "https://api.exchangerate.host/"

    // Retrofit setup using CurrencyApi
    val api: CurrencyApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())  // Gson converter
            .build()
            .create(CurrencyApi::class.java)  // Create API instance
    }
}

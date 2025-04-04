package com.zybooks.androidassignments.network

import com.zybooks.androidassignments.model.CurrencyResponse
import retrofit2.http.GET

interface CurrencyApi {
    @GET("v6/latest/USD")
    suspend fun getRates(): CurrencyResponse
}

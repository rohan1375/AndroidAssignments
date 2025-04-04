package com.zybooks.androidassignments.model

data class CurrencyResponse(
    val rates: Map<String, Double>  // Map of currency code to exchange rate
)

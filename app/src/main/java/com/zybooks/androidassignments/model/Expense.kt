package com.zybooks.androidassignments.model

data class Expense(
    val name: String,
    val totalAmount: String,
    var currency: String = "CAD",
    var convertedCost: Double = 0.0
)

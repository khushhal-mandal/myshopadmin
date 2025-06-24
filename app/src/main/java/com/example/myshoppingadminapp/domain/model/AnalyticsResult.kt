package com.example.myshoppingadminapp.domain.model

data class AnalyticsResult(
    val mostSoldCategory: String = "",
    val mostProfitableCategory: String = "",
    val totalOrders: Int = 0,
    val totalRevenue: Int = 0,
    val averageOrderValue: Int = 0,
    val quantityPerCategory: Map<String, Int> = emptyMap(),
    val revenuePerCategory: Map<String, Int> = emptyMap(),
    val topProductsByQuantity: List<Pair<String, Int>> = emptyList(),
    val topProductsByRevenue: List<Pair<String, Int>> = emptyList(),
    val ordersPerDay: Map<String, Int> = emptyMap()
)

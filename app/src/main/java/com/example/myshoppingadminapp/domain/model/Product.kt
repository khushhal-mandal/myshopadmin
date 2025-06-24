package com.example.myshoppingadminapp.domain.model

data class Product (
    val id: String = "",
    var name: String = "",
    val price: String = "",
    val finalPrice: String = "",
    val category: String = "",
    val description: String = "",
    val availableUnits: String = "",
    val image: String = ""
)
package com.example.myshoppingadminapp.domain.model

data class Order (
    val orderId: String = "",
    val time: Long = 0L,
    val products: List<Cart> = emptyList(),
    val totalPrice: Int = 0,
    val shippingInfo: ShippingInfo = ShippingInfo()
)



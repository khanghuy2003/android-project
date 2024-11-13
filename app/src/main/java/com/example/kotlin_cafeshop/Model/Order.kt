package com.example.kotlin_cafeshop.Model

data class Order(
    val orderId: String="",
    var name: String="",
    var address: String="",
    var phone: String="",
    val paymentMethod: String="",
    val orderItems: List<CartItem> = emptyList(),
    val orderDateTime: String="",
    var status: String="",
    val totalPaymentOrder:String=""
)


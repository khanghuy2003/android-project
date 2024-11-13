package com.example.kotlin_cafeshop.Model

    data class User(
        val userId: String? = null,  // ID người dùng, sẽ lấy từ Firebase Authentication
        val email: String? = null,  // Địa chỉ email của người dùng
        val cart: MutableList<CartItem> = mutableListOf(),  // Giỏ hàng của người dùng
        val orders: MutableList<Order> = mutableListOf()  // Danh sách đơn hàng của người dùng
    )

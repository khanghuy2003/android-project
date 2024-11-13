package com.example.kotlin_cafeshop.Model

data class CartItem(
    val id:String?=null,
    val cartItemId: String?=null,
    val cartItemName: String?=null,
    val cartItemQuantity: Int?=0,
    val cartItemSize: String?=null,
    val cartItemTotalPrice: Int?=0,
    val cartItemImageUrl:String?=null
)
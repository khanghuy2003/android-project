package com.example.kotlin_cafeshop.Model

import java.io.Serializable

class Products(
    val product_id:String?=null,
    val product_name:String?=null,
    val product_imageurl:String?=null,
    val product_categoryid:Int?=null,
    val product_price:Int?=null,
    val product_salescount:Int?=null
    ):Serializable
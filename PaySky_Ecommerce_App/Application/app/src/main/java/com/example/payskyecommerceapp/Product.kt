package com.example.payskyecommerceapp

import com.google.gson.annotations.SerializedName

data class Product(
    @SerializedName("id") val id: String = "",
    @SerializedName("name") val name: String = "",
    @SerializedName("description") val description: String = "",
    @SerializedName("imageUrl") val imageUrl: String = "",
    @SerializedName("price") val price: Double = 0.0
)
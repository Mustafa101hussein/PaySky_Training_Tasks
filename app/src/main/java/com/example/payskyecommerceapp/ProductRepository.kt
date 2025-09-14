package com.example.payskyecommerceapp

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface ProductApiService {
    @GET("products")
    suspend fun getProducts(): List<Product>
}

class ProductRepository {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://5668012a-6a7d-4057-b670-dfc5dcc166c6.mock.pstmn.io/") // REPLACE WITH YOUR MOCK API URL
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val productApiService = retrofit.create(ProductApiService::class.java)

    suspend fun getProducts(): List<Product> {
        return try {
            productApiService.getProducts()
        } catch (e: Exception) {
            emptyList()
        }
    }
}
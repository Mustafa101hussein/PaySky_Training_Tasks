package com.example.payskyecommerceapp

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

data class PaymentIntentRequest(
    val amount: Long,
    val currency: String = "usd"
)

data class PaymentIntentResponse(
    val clientSecret: String,
    val paymentIntentId: String?
)

interface PaymentService {
    @POST("create-payment-intent")
    suspend fun createPaymentIntent(@Body request: PaymentIntentRequest): PaymentIntentResponse
}

object BackendService {
    // For Android Emulator use:
    private const val BASE_URL = "http://10.0.2.2:3000/"

    // For physical device on same WiFi use your computer's IP:
    // private const val BASE_URL = "http://192.168.1.100:3000/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val paymentService = retrofit.create(PaymentService::class.java)

    suspend fun createPaymentIntent(amount: Long): String {
        try {
            val response = paymentService.createPaymentIntent(PaymentIntentRequest(amount))
            return response.clientSecret
        } catch (e: Exception) {
            println("Backend error: ${e.message}")
            // Fallback for demo
            return "pi_mock_client_secret_${System.currentTimeMillis()}"
        }
    }
}
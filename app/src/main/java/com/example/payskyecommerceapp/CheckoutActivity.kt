package com.example.payskyecommerceapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import com.example.payskyecommerceapp.databinding.ActivityCheckoutBinding

class CheckoutActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCheckoutBinding
    private lateinit var paymentSheet: PaymentSheet
    private lateinit var customerConfig: PaymentSheet.CustomerConfiguration
    private lateinit var paymentIntentClientSecret: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Stripe
        PaymentConfiguration.init(this, "pk_test_your_publishable_key") // Replace with your test key

        paymentSheet = PaymentSheet(this, ::onPaymentSheetResult)

        binding.totalAmountText.text = "Total: $${String.format("%.2f", CartManager.getTotalPrice())}"

        binding.payButton.setOnClickListener {
            // In a real app, you would fetch the payment intent from your server
            // For this example, we'll simulate creating a payment intent
            simulatePaymentIntentCreation()
        }
    }

    private fun simulatePaymentIntentCreation() {
        // In a real implementation, you would:
        // 1. Call your backend to create a PaymentIntent
        // 2. Get the client secret from the response
        // 3. Present the payment sheet

        // For demonstration, we'll use a mock client secret
        // In a real app, this should come from your server
        paymentIntentClientSecret = "pi_1MockClientSecret_123" // This should be a real client secret from your server

        // Present the payment sheet
        presentPaymentSheet()
    }

    private fun presentPaymentSheet() {
        val configuration = PaymentSheet.Configuration("Example, Inc.")

        paymentSheet.presentWithPaymentIntent(
            paymentIntentClientSecret,
            configuration
        )
    }

    private fun onPaymentSheetResult(paymentResult: PaymentSheetResult) {
        when (paymentResult) {
            is PaymentSheetResult.Completed -> {
                // Payment succeeded
                showSuccessMessage()
                CartManager.clearCart()
            }
            is PaymentSheetResult.Canceled -> {
                // Payment was canceled
                showCancelMessage()
            }
            is PaymentSheetResult.Failed -> {
                // Payment failed
                showErrorMessage(paymentResult.error.message ?: "Unknown error")
            }
        }
    }

    private fun showSuccessMessage() {
        android.app.AlertDialog.Builder(this)
            .setTitle("Payment Successful")
            .setMessage("Thank you for your purchase!")
            .setPositiveButton("OK") { dialog, which ->
                finish()
            }
            .show()
    }

    private fun showCancelMessage() {
        android.app.AlertDialog.Builder(this)
            .setTitle("Payment Canceled")
            .setMessage("Your payment was canceled.")
            .setPositiveButton("OK", null)
            .show()
    }

    private fun showErrorMessage(message: String) {
        android.app.AlertDialog.Builder(this)
            .setTitle("Payment Failed")
            .setMessage("Error: $message")
            .setPositiveButton("OK", null)
            .show()
    }
}
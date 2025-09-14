package com.example.payskyecommerceapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult



class CartActivity : AppCompatActivity() {
    private lateinit var cartListView: ListView
    private lateinit var totalPriceText: TextView
    private lateinit var emptyCartText: TextView
    private lateinit var checkoutButton: Button
    private lateinit var continueShoppingButton: Button

    // Stripe variables - ADD THESE
    private lateinit var paymentSheet: PaymentSheet
    private var paymentIntentClientSecret: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        initializeViews()

        // Initialize Stripe with your TEST publishable key - ADD THIS
        PaymentConfiguration.init(this, "pk_test_51S3D7MFHbWtrG5M6dSIcc5VMZCnI6vUm4jBdHJPhHDBr5OYrseZg7DQqIdlBfgF1ao7ZlMq67BHM4Lb57KDwBSen00QVdovLtz")
        paymentSheet = PaymentSheet(this, ::onPaymentSheetResult)

        setupCartListView()
        updateUI()
        setupClickListeners()
    }

    private fun initializeViews() {
        cartListView = findViewById(R.id.cartListView)
        totalPriceText = findViewById(R.id.totalPriceText)
        emptyCartText = findViewById(R.id.emptyCartText)
        checkoutButton = findViewById(R.id.checkoutButton)
        continueShoppingButton = findViewById(R.id.continueShoppingButton)
    }

    private fun setupClickListeners() {
        checkoutButton.setOnClickListener {
            startCheckoutProcess()
        }

        continueShoppingButton.setOnClickListener {
            finish()
        }
    }

    private fun setupCartListView() {
        val adapter = CartAdapter(this, CartManager.getCartItems()) { productId ->
            CartManager.removeItemCompletely(productId)
            updateUI()
        }
        cartListView.adapter = adapter
    }

    private fun updateUI() {
        val cartItems = CartManager.getCartItems()
        emptyCartText.isVisible = cartItems.isEmpty()
        cartListView.isVisible = cartItems.isNotEmpty()
        checkoutButton.isVisible = cartItems.isNotEmpty()
        totalPriceText.text = "Total: $${String.format("%.2f", CartManager.getTotalPrice())}"
    }

    private fun startCheckoutProcess() {
        val totalAmount = (CartManager.getTotalPrice() * 100).toLong()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Use real backend service
                paymentIntentClientSecret = BackendService.createPaymentIntent(totalAmount)

                withContext(Dispatchers.Main) {
                    paymentIntentClientSecret?.let {
                        presentPaymentSheet()
                    } ?: showErrorDialog("Failed to initialize payment")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showErrorDialog("Failed to connect to payment service: ${e.message}")
                }
            }
        }
    }

    private fun presentPaymentSheet() {
        val configuration = PaymentSheet.Configuration(
            "My E-commerce Store",
            allowsDelayedPaymentMethods = true
        )

        paymentIntentClientSecret?.let { clientSecret ->
            paymentSheet.presentWithPaymentIntent(clientSecret, configuration)
        }
    }

    private fun onPaymentSheetResult(paymentResult: PaymentSheetResult) {
        when (paymentResult) {
            is PaymentSheetResult.Completed -> {
                // Payment succeeded - navigate to confirmation screen
                val intent = Intent(this, OrderConfirmationActivity::class.java).apply {
                    putExtra("ORDER_TOTAL", CartManager.getTotalPrice())
                }
                startActivity(intent)
                CartManager.clearCart()
                finish()
            }
            is PaymentSheetResult.Canceled -> {
                showInfoDialog("Payment Canceled", "Your payment was canceled.")
            }
            is PaymentSheetResult.Failed -> {
                showErrorDialog("Payment Failed: ${paymentResult.error.message ?: "Unknown error"}")
            }
        }
    }

    private fun showErrorDialog(message: String) {
        android.app.AlertDialog.Builder(this)
            .setTitle("Error")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }

    private fun showInfoDialog(title: String, message: String) {
        android.app.AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }

    override fun onResume() {
        super.onResume()
        updateUI()
    }
}
package com.example.payskyecommerceapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class OrderConfirmationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_confirmation)

        val orderTotal = intent.getDoubleExtra("ORDER_TOTAL", 0.0)
        val confirmationMessage = findViewById<TextView>(R.id.confirmationMessage)
        val continueShoppingButton = findViewById<Button>(R.id.continueShoppingButton)

        confirmationMessage.text =
            "Thank you for your order!\n\nOrder Total: $${String.format("%.2f", orderTotal)}\n\nYour order will be processed shortly."

        continueShoppingButton.setOnClickListener {
            val intent = Intent(this, ProductListActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }
    }
}
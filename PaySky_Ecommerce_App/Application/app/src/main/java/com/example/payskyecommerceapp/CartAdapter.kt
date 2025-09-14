package com.example.payskyecommerceapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView

class CartAdapter(
    private val context: Context,
    private val cartItems: List<CartItem>,
    private val onRemove: (String) -> Unit
) : ArrayAdapter<CartItem>(context, 0, cartItems) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val cartItem = cartItems[position]
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_cart, parent, false)

        view.findViewById<TextView>(R.id.cartItemName).text = cartItem.product.name
        view.findViewById<TextView>(R.id.cartItemPrice).text = "$${String.format("%.2f", cartItem.product.price)}" // FIXED
        view.findViewById<TextView>(R.id.cartItemQuantity).text = "Qty: ${cartItem.quantity}" // FIXED from "Oty" to "Qty"
        view.findViewById<TextView>(R.id.cartItemTotal).text = "Total: $${String.format("%.2f", cartItem.product.price * cartItem.quantity)}" // FIXED

        view.findViewById<Button>(R.id.removeButton).setOnClickListener {
            onRemove(cartItem.product.id)
        }

        return view
    }
}
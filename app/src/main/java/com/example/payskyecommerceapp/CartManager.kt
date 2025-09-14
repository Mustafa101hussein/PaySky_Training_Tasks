package com.example.payskyecommerceapp

data class CartItem(
    val product: Product,
    var quantity: Int = 1
)

object CartManager {
    private val cartItems = mutableListOf<CartItem>()

    fun addToCart(product: Product) {
        val existingItem = cartItems.find { it.product.id == product.id }
        if (existingItem != null) {
            existingItem.quantity++
        } else {
            cartItems.add(CartItem(product))
        }
    }

    fun removeFromCart(productId: String) {
        val item = cartItems.find { it.product.id == productId }
        if (item != null) {
            if (item.quantity > 1) {
                item.quantity--
            } else {
                cartItems.remove(item)
            }
        }
    }

    fun removeItemCompletely(productId: String) {
        cartItems.removeAll { it.product.id == productId }
    }

    fun getCartItems(): List<CartItem> = cartItems.toList()

    fun getTotalPrice(): Double {
        return cartItems.sumOf { it.product.price * it.quantity }
    }

    fun getCartItemCount(): Int {
        return cartItems.sumOf { it.quantity }
    }

    fun clearCart() {
        cartItems.clear()
    }
}
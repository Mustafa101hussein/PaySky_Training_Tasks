package com.example.payskyecommerceapp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import coil.load
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ProductListActivity : AppCompatActivity() {
    private val viewModel: ProductViewModel by viewModels()
    private lateinit var productsListView: ListView
    private lateinit var progressBar: ProgressBar
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var cartBadge: BadgeDrawable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_list)

        productsListView = findViewById(R.id.productsListView)
        progressBar = findViewById(R.id.progressBar)
        bottomNavigation = findViewById(R.id.bottomNavigation)

        // Setup cart badge
        cartBadge = bottomNavigation.getOrCreateBadge(R.id.cartFragment)
        updateCartBadge()

        // Observe ViewModel states
        lifecycleScope.launch {
            viewModel.isLoading.collectLatest { isLoading ->
                progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            }
        }

        lifecycleScope.launch {
            viewModel.error.collectLatest { error ->
                error?.let {
                    Snackbar.make(productsListView, it, Snackbar.LENGTH_LONG).show()
                }
            }
        }

        lifecycleScope.launch {
            viewModel.products.collectLatest { products ->
                if (products.isNotEmpty()) {
                    val adapter = ProductAdapter(this@ProductListActivity, products)
                    productsListView.adapter = adapter
                }
            }
        }

        // Setup bottom navigation
        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.cartFragment -> {
                    startActivity(Intent(this, CartActivity::class.java))
                    true
                }
                else -> false
            }
        }

        // Load products
        viewModel.loadProducts()
    }

    private fun updateCartBadge() {
        val count = CartManager.getCartItemCount()
        if (count > 0) {
            cartBadge.number = count
            cartBadge.isVisible = true
        } else {
            cartBadge.isVisible = false
        }
    }

    override fun onResume() {
        super.onResume()
        updateCartBadge()
    }

    private inner class ProductAdapter(
        context: Context,
        private val products: List<Product>
    ) : ArrayAdapter<Product>(context, 0, products) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val product = products[position]
            val view = convertView ?: LayoutInflater.from(context)
                .inflate(R.layout.item_product, parent, false)

            view.findViewById<TextView>(R.id.productName).text = product.name
            view.findViewById<TextView>(R.id.productDescription).text = product.description
            view.findViewById<TextView>(R.id.productPrice).text = "$${product.price}"

            view.findViewById<ImageView>(R.id.productImage).load(product.imageUrl) {
                placeholder(R.drawable.no_image)
                error(R.drawable.error_image)
            }

            view.findViewById<Button>(R.id.addToCartButton).setOnClickListener {
                CartManager.addToCart(product)
                updateCartBadge()
                Snackbar.make(productsListView, "${product.name} added to cart", Snackbar.LENGTH_SHORT).show()
            }

            return view
        }
    }
}
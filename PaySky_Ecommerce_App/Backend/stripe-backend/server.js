const express = require('express');
const cors = require('cors');
require('dotenv').config(); // MUST be at the VERY TOP

// Now initialize Stripe AFTER dotenv is configured
const stripe = require('stripe')(process.env.STRIPE_SECRET_KEY);

const app = express();
const PORT = process.env.PORT || 3000;

// Debug: Check if environment variables are loaded
console.log('PORT:', process.env.PORT);
console.log('Stripe Key Available:', !!process.env.STRIPE_SECRET_KEY);
console.log('Stripe Key Length:', process.env.STRIPE_SECRET_KEY ? process.env.STRIPE_SECRET_KEY.length : 'Not loaded');

// Middleware
app.use(cors());
app.use(express.json());

// Health check endpoint
app.get('/', (req, res) => {
  res.json({ 
    message: 'Stripe Backend is running!',
    stripeKeyLoaded: !!process.env.STRIPE_SECRET_KEY,
    port: process.env.PORT
  });
});

// Create PaymentIntent endpoint
app.post('/create-payment-intent', async (req, res) => {
  try {
    const { amount, currency = 'usd' } = req.body;

    console.log('Creating PaymentIntent for amount:', amount);

    // Validate amount
    if (!amount || amount < 1) {
      return res.status(400).json({ error: 'Invalid amount' });
    }

    // Create PaymentIntent
    const paymentIntent = await stripe.paymentIntents.create({
      amount: amount,
      currency: currency,
      automatic_payment_methods: {
        enabled: true,
      },
      metadata: {
        integration_check: 'accept_a_payment'
      }
    });

    console.log('PaymentIntent created:', paymentIntent.id);

    // Send client secret to frontend
    res.json({
      clientSecret: paymentIntent.client_secret,
      paymentIntentId: paymentIntent.id,
      amount: paymentIntent.amount,
      currency: paymentIntent.currency
    });

  } catch (error) {
    console.error('Error creating PaymentIntent:', error);
    res.status(500).json({ 
      error: error.message,
      details: 'Check your Stripe secret key'
    });
  }
});

// Start server
app.listen(PORT, () => {
  console.log(`=== Server Started ===`);
  console.log(`Server running on http://localhost:${PORT}`);
  console.log(`Stripe Key Loaded: ${!!process.env.STRIPE_SECRET_KEY}`);
  console.log(`Test endpoints:`);
  console.log(`- GET  http://localhost:${PORT}/`);
  console.log(`- POST http://localhost:${PORT}/create-payment-intent`);
  console.log(`========================`);
});
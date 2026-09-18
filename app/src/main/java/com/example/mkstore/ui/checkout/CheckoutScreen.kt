package com.example.mkstore.ui.checkout

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    viewModel: CheckoutViewModel,
    onBackClick: () -> Unit,
    onOrderSuccess: () -> Unit
) {
    val orderPlaced by viewModel.orderPlaced.collectAsState()
    var address by remember { mutableStateOf("123 Main Street, City") }
    var cardNumber by remember { mutableStateOf("4242 •••• •••• 4242") }

    if (orderPlaced) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text("Order Placed Successfully!") },
            text = { Text("Thank you for your purchase. Your order has been placed.") },
            confirmButton = {
                Button(onClick = onOrderSuccess) {
                    Text("Back to Home")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Checkout") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(text = "Shipping Address", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Address") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Payment Method", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = cardNumber,
                    onValueChange = { cardNumber = it },
                    label = { Text("Card Number") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Button(
                onClick = { viewModel.placeOrder() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Place Order")
            }
        }
    }
}

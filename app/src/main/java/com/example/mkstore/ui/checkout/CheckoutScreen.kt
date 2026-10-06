package com.example.mkstore.ui.checkout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    viewModel: CheckoutViewModel,
    onBackClick: () -> Unit,
    onOrderSuccess: () -> Unit
) {
    val orderPlaced by viewModel.orderPlaced.collectAsState()
    var currentStep by remember { mutableIntStateOf(1) } // 1: Shipping, 2: Payment, 3: Review

    // Form states
    var fullName by remember { mutableStateOf("Karan Kumar") }
    var phone by remember { mutableStateOf("+91 9876543210") }
    var streetAddress by remember { mutableStateOf("123 Green Park Street, Apt 4B") }
    var city by remember { mutableStateOf("New Delhi") }
    var postalCode by remember { mutableStateOf("110001") }

    var paymentOption by remember { mutableStateOf("UPI / Google Pay") }
    var paymentDetail by remember { mutableStateOf("karan@okicici") }

    val orderId = remember { "#MKS-" + (10000..99999).random() }

    if (orderPlaced) {
        AlertDialog(
            onDismissRequest = { },
            icon = {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = "Success",
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(56.dp)
                )
            },
            title = {
                Text(
                    text = "Order Placed Successfully! 🎉",
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Order ID: $orderId",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "Estimated Delivery: Within 2-3 Business Days",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(text = "Delivering To:", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.labelMedium)
                            Text(text = "$fullName, $streetAddress, $city - $postalCode", style = MaterialTheme.typography.bodySmall)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "Payment Option: $paymentOption", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = onOrderSuccess,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Continue Shopping")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Checkout Stepper", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                // Stepper Header Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StepItem(stepNumber = 1, title = "Address", isActive = currentStep == 1, isDone = currentStep > 1)
                    HorizontalDivider(modifier = Modifier.weight(1f).padding(horizontal = 8.dp))
                    StepItem(stepNumber = 2, title = "Payment", isActive = currentStep == 2, isDone = currentStep > 2)
                    HorizontalDivider(modifier = Modifier.weight(1f).padding(horizontal = 8.dp))
                    StepItem(stepNumber = 3, title = "Review", isActive = currentStep == 3, isDone = currentStep > 3)
                }

                Spacer(modifier = Modifier.height(24.dp))

                when (currentStep) {
                    1 -> {
                        // Step 1: Shipping Address Form
                        Text(text = "Step 1: Shipping Address", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = fullName,
                            onValueChange = { fullName = it },
                            label = { Text("Full Name") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = { Text("Phone Number") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = streetAddress,
                            onValueChange = { streetAddress = it },
                            label = { Text("Street Address") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = city,
                                onValueChange = { city = it },
                                label = { Text("City") },
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = postalCode,
                                onValueChange = { postalCode = it },
                                label = { Text("Postal Code") },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    2 -> {
                        // Step 2: Payment Method Selection
                        Text(text = "Step 2: Select Payment Method", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilterChip(
                                selected = paymentOption == "UPI / Google Pay",
                                onClick = {
                                    paymentOption = "UPI / Google Pay"
                                    paymentDetail = "karan@okicici"
                                },
                                label = { Text("UPI / GPay") }
                            )
                            FilterChip(
                                selected = paymentOption == "Credit/Debit Card",
                                onClick = {
                                    paymentOption = "Credit/Debit Card"
                                    paymentDetail = "4242 •••• •••• 4242"
                                },
                                label = { Text("Card") }
                            )
                            FilterChip(
                                selected = paymentOption == "Cash on Delivery",
                                onClick = {
                                    paymentOption = "Cash on Delivery"
                                    paymentDetail = "Pay on delivery"
                                },
                                label = { Text("COD") }
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = paymentDetail,
                            onValueChange = { paymentDetail = it },
                            label = { Text(if (paymentOption.contains("UPI")) "UPI ID" else "Card Details") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    3 -> {
                        // Step 3: Order Review Summary
                        Text(text = "Step 3: Review Order & Confirm", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(12.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Home, contentDescription = "Address", tint = MaterialTheme.colorScheme.primary)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(text = "Shipping Address", fontWeight = FontWeight.Bold)
                                }
                                Text(text = "$fullName ($phone)", style = MaterialTheme.typography.bodySmall)
                                Text(text = "$streetAddress, $city - $postalCode", style = MaterialTheme.typography.bodySmall)

                                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CreditCard, contentDescription = "Payment", tint = MaterialTheme.colorScheme.primary)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(text = "Payment Method", fontWeight = FontWeight.Bold)
                                }
                                Text(text = "$paymentOption ($paymentDetail)", style = MaterialTheme.typography.bodySmall)

                                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.AutoMirrored.Filled.ReceiptLong, contentDescription = "Summary", tint = MaterialTheme.colorScheme.primary)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(text = "Order Costs", fontWeight = FontWeight.Bold)
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(text = "Standard Shipping:", style = MaterialTheme.typography.bodySmall)
                                    Text(text = "FREE", style = MaterialTheme.typography.bodySmall, color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold)
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(text = "Estimated Tax (GST):", style = MaterialTheme.typography.bodySmall)
                                    Text(text = "$0.00", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Navigation buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (currentStep > 1) {
                    OutlinedButton(
                        onClick = { currentStep-- },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("← Back")
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                }

                Button(
                    onClick = {
                        if (currentStep < 3) {
                            currentStep++
                        } else {
                            viewModel.placeOrder()
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (currentStep == 3) "Confirm & Place Order 🎉" else "Next Step ➔", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun StepItem(
    stepNumber: Int,
    title: String,
    isActive: Boolean,
    isDone: Boolean
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(
                    when {
                        isDone || isActive -> MaterialTheme.colorScheme.primary
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (isDone) "✓" else "$stepNumber",
                color = if (isDone || isActive) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            color = if (isActive || isDone) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal
        )
    }
}

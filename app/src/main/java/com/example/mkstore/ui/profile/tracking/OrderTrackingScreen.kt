package com.example.mkstore.ui.profile.tracking

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderTrackingScreen(
    orderId: String,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Track Order: $orderId", fontWeight = FontWeight.Bold) },
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
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(text = "Order Reference: $orderId", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(text = "Status: Out for Delivery 🚚", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                    Text(text = "Estimated Delivery: Today by 7:00 PM", style = MaterialTheme.typography.bodySmall)
                }
            }

            Text(text = "Live Shipment Timeline", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(8.dp))

            TrackingStepItem(
                title = "Order Placed & Verified",
                subtitle = "We have received your order and payment.",
                isCompleted = true,
                isCurrent = false
            )
            TrackingStepItem(
                title = "Packed at Fulfillment Center",
                subtitle = "Items carefully packaged and quality inspected.",
                isCompleted = true,
                isCurrent = false
            )
            TrackingStepItem(
                title = "Shipped via Express Logistics",
                subtitle = "In transit to your nearest local delivery hub.",
                isCompleted = true,
                isCurrent = true
            )
            TrackingStepItem(
                title = "Out for Delivery",
                subtitle = "Delivery executive is on the way to your address.",
                isCompleted = false,
                isCurrent = false
            )
            TrackingStepItem(
                title = "Delivered Successfully",
                subtitle = "Handed over to customer.",
                isCompleted = false,
                isCurrent = false
            )
        }
    }
}

@Composable
fun TrackingStepItem(
    title: String,
    subtitle: String,
    isCompleted: Boolean,
    isCurrent: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(
                modifier = Modifier.size(32.dp),
                shape = MaterialTheme.shapes.extraLarge,
                color = if (isCompleted || isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Default.LocalShipping,
                        contentDescription = title,
                        tint = if (isCompleted || isCurrent) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            HorizontalDivider(modifier = Modifier.width(2.dp).height(40.dp), color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = if (isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}

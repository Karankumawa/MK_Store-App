package com.example.mkstore.ui.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    viewModel: SplashViewModel,
    onNavigateToOnboarding: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    LaunchedEffect(key1 = true) {
        delay(1500L)
        if (viewModel.sessionManager.isFirstRun()) {
            onNavigateToOnboarding()
        } else {
            onNavigateToHome()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "M K Store",
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 38.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "EST. 2025",
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

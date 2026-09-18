package com.example.mkstore.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel,
    onFinishOnboarding: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                val title = when (page) {
                    0 -> "Discover Latest Products"
                    1 -> "Secure & Fast Checkout"
                    else -> "Enjoy Your Shopping"
                }
                val description = when (page) {
                    0 -> "Explore thousands of products across multiple categories with ease."
                    1 -> "Fast and secure checkout experience with instant order tracking."
                    else -> "Get the best deals delivered right to your doorstep."
                }

                Text(
                    text = title,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = description,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(3) { iteration ->
                val color = if (pagerState.currentPage == iteration) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .size(if (pagerState.currentPage == iteration) 12.dp else 8.dp)
                        .clip(CircleShape)
                        .background(color)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (pagerState.currentPage < 2) {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                } else {
                    viewModel.completeOnboarding()
                    onFinishOnboarding()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = if (pagerState.currentPage == 2) "Get Started" else "Next")
        }
    }
}

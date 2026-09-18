package com.example.mkstore.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.mkstore.ui.auth.LoginScreen
import com.example.mkstore.ui.auth.LoginViewModel
import com.example.mkstore.ui.cart.CartScreen
import com.example.mkstore.ui.cart.CartViewModel
import com.example.mkstore.ui.checkout.CheckoutScreen
import com.example.mkstore.ui.checkout.CheckoutViewModel
import com.example.mkstore.ui.home.HomeScreen
import com.example.mkstore.ui.home.HomeViewModel
import com.example.mkstore.ui.onboarding.OnboardingScreen
import com.example.mkstore.ui.product_detail.ProductDetailScreen
import com.example.mkstore.ui.product_detail.ProductDetailViewModel
import com.example.mkstore.ui.profile.ProfileScreen
import com.example.mkstore.ui.splash.SplashScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToOnboarding = {
                    navController.navigate(Screen.Onboarding.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onFinishOnboarding = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Login.route) {
            val viewModel: LoginViewModel = hiltViewModel()
            LoginScreen(
                viewModel = viewModel,
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Home.route) {
            val viewModel: HomeViewModel = hiltViewModel()
            HomeScreen(
                viewModel = viewModel,
                onProductClick = { productId ->
                    navController.navigate(Screen.ProductDetail.createRoute(productId))
                },
                onCartClick = {
                    navController.navigate(Screen.Cart.route)
                },
                onProfileClick = {
                    navController.navigate(Screen.Profile.route)
                }
            )
        }
        composable(
            route = Screen.ProductDetail.route,
            arguments = listOf(navArgument("productId") { type = NavType.StringType })
        ) {
            val viewModel: ProductDetailViewModel = hiltViewModel()
            ProductDetailScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                onCartClick = { navController.navigate(Screen.Cart.route) }
            )
        }
        composable(Screen.Cart.route) {
            val viewModel: CartViewModel = hiltViewModel()
            CartScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                onCheckoutClick = { navController.navigate(Screen.Checkout.route) }
            )
        }
        composable(Screen.Checkout.route) {
            val viewModel: CheckoutViewModel = hiltViewModel()
            CheckoutScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                onOrderSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Profile.route) {
            ProfileScreen(
                onBackClick = { navController.popBackStack() },
                onLogoutClick = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }
    }
}

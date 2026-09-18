package com.example.mkstore.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.mkstore.ui.auth.AuthViewModel
import com.example.mkstore.ui.auth.LoginScreen
import com.example.mkstore.ui.cart.CartScreen
import com.example.mkstore.ui.cart.CartViewModel
import com.example.mkstore.ui.checkout.CheckoutScreen
import com.example.mkstore.ui.checkout.CheckoutViewModel
import com.example.mkstore.ui.home.HomeScreen
import com.example.mkstore.ui.home.HomeViewModel
import com.example.mkstore.ui.onboarding.OnboardingScreen
import com.example.mkstore.ui.onboarding.OnboardingViewModel
import com.example.mkstore.ui.product_detail.ProductDetailScreen
import com.example.mkstore.ui.product_detail.ProductDetailViewModel
import com.example.mkstore.ui.profile.ProfileScreen
import com.example.mkstore.ui.splash.SplashScreen
import com.example.mkstore.ui.splash.SplashViewModel

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            val viewModel: SplashViewModel = hiltViewModel()
            SplashScreen(
                viewModel = viewModel,
                onNavigateToOnboarding = {
                    navController.navigate(Screen.Onboarding.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Onboarding.route) {
            val viewModel: OnboardingViewModel = hiltViewModel()
            OnboardingScreen(
                viewModel = viewModel,
                onFinishOnboarding = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Login.route) {
            val viewModel: AuthViewModel = hiltViewModel()
            LoginScreen(
                viewModel = viewModel,
                onLoginSuccess = {
                    navController.popBackStack()
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
                onCheckoutClick = { isLoggedIn: Boolean ->
                    if (isLoggedIn) {
                        navController.navigate(Screen.Checkout.route)
                    } else {
                        navController.navigate(Screen.Login.route)
                    }
                }
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
            val viewModel: AuthViewModel = hiltViewModel()
            ProfileScreen(
                authViewModel = viewModel,
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

package com.example.mkstore.ui.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object Login : Screen("login")
    object Home : Screen("home")
    object Favorites : Screen("favorites")
    object ProductDetail : Screen("product_detail/{productId}") {
        fun createRoute(productId: Int) = "product_detail/$productId"
    }
    object Cart : Screen("cart")
    object Checkout : Screen("checkout")
    object Profile : Screen("profile")
    object MyOrders : Screen("my_orders")
    object ShippingAddresses : Screen("shipping_addresses")
    object PaymentMethods : Screen("payment_methods")
    object OrderTracking : Screen("order_tracking/{orderId}") {
        fun createRoute(orderId: String) = "order_tracking/$orderId"
    }
    object Coupons : Screen("coupons")
    object Support : Screen("support")
    object AdminDashboard : Screen("admin_dashboard")
}

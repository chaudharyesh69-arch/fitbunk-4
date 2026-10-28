package com.example.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Welcome : Screen("welcome")
    object Auth : Screen("auth")
    object ProfileSetup : Screen("profile_setup")
    object SizePreferences : Screen("size_preferences")
    object LocationSetup : Screen("location_setup")

    // Main bottom tabs
    object Discover : Screen("discover")
    object Search : Screen("search")
    object Liked : Screen("liked")
    object Cart : Screen("cart")
    object Profile : Screen("profile")

    // Sub-screens & Modals
    data class ProductDetail(val productId: String) : Screen("product_detail/$productId") {
        companion object {
            const val ROUTE = "product_detail"
        }
    }
    object Checkout : Screen("checkout")
    data class OrderSuccess(val orderId: String) : Screen("order_success/$orderId")
    data class OrderTracking(val orderId: String) : Screen("order_tracking/$orderId")
    object SettingsAndSupport : Screen("settings_and_support")
}

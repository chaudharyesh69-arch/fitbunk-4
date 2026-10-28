package com.example.model

enum class ProductCategory(val label: String) {
    ALL("All Fits"),
    T_SHIRTS("T-Shirts"),
    HOODIES("Hoodies"),
    SHIRTS("Shirts"),
    JEANS("Jeans"),
    TROUSERS("Trousers"),
    JACKETS("Jackets"),
    SNEAKERS("Sneakers"),
    ACCESSORIES("Accessories")
}

enum class StyleTag(val label: String, val iconText: String) {
    STREETWEAR("Streetwear", "⚡"),
    CASUAL("Casual", "✌"),
    MINIMAL("Minimal", "✦"),
    SPORTY("Sporty", "🔥"),
    TRENDY("Trendy", "✨")
}

data class ProductColor(
    val name: String,
    val hex: Long
)

data class BrandInfo(
    val name: String,
    val tagline: String,
    val origin: String,
    val aesthetic: String,
    val logoText: String,
    val accentColor: Long,
    val totalFits: Int = 100
)

data class CartItem(
    val id: String,
    val product: Product,
    val selectedSize: String,
    val selectedColor: ProductColor,
    var quantity: Int = 1
)

data class DeliveryAddress(
    val id: String,
    val label: String, // Home, Work, Campus
    val houseFlat: String,
    val street: String,
    val area: String,
    val city: String,
    val state: String,
    val pinCode: String,
    val isDefault: Boolean = false
) {
    val fullAddressText: String
        get() = "$houseFlat, $street, $area, $city, $state - $pinCode"
}

enum class OrderStatus(val title: String, val stepNumber: Int, val description: String) {
    PLACED("Order Placed", 1, "Your fit is being processed"),
    CONFIRMED("Confirmed", 2, "Verified & allocated at nearby dark store"),
    PACKED("Packed", 3, "Eco-sealed & bagged by the FITBUNK squad"),
    OUT_FOR_DELIVERY("Out for Delivery", 4, "Rider is speeding through traffic"),
    DELIVERED("Delivered", 5, "Delivered to your door. Time to flex!")
}

data class Order(
    val id: String,
    val items: List<CartItem>,
    val subtotal: Int,
    val discountAmount: Int,
    val deliveryFee: Int,
    val totalAmount: Int,
    val deliveryAddress: DeliveryAddress,
    val paymentMethod: String,
    val status: OrderStatus = OrderStatus.PLACED,
    val timestamp: Long = System.currentTimeMillis(),
    val estimatedArrivalMins: Int = 45,
    val riderName: String = "Aarav Sharma",
    val riderPhone: String = "+91 98765 43210"
)

data class UserProfile(
    val name: String = "Alex Rivera",
    val email: String = "alex.rivera@fitbunk.io",
    val ageConfirmed: Boolean = true,
    val preferredCategories: Set<ProductCategory> = setOf(
        ProductCategory.HOODIES,
        ProductCategory.T_SHIRTS,
        ProductCategory.JEANS
    ),
    val preferredStyle: StyleTag = StyleTag.STREETWEAR,
    val budgetPreference: String = "₹1500 - ₹3500",
    val sizeTops: String = "L",
    val sizeBottoms: String = "32",
    val sizeShoes: String = "UK 9",
    val addresses: List<DeliveryAddress> = listOf(
        DeliveryAddress(
            id = "addr_1",
            label = "Home",
            houseFlat = "Flat 402, Skyline Residency",
            street = "12th Main, 4th Block",
            area = "Koramangala",
            city = "Bengaluru",
            state = "Karnataka",
            pinCode = "560034",
            isDefault = true
        )
    ),
    val selectedAddressId: String = "addr_1",
    val pushNotificationsEnabled: Boolean = true,
    val orderAlertsEnabled: Boolean = true,
    val dropAlertsEnabled: Boolean = true
)

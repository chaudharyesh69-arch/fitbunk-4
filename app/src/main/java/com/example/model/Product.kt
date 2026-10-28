package com.example.model

/**
 * Product data class to manage our 1,000 product inventory across streetwear brands.
 *
 * @param id Unique product identifier (e.g., "fb_0001")
 * @param name Product title and cut descriptor
 * @param brand Designer fashion label (e.g., "VOID STUDIO", "NEO-ARCHIVE")
 * @param price Retail price in Indian Rupees (₹)
 * @param imageUrl High-resolution product hero image URL
 * @param category Product clothing category (HOODIES, TEES, CARGOS, etc.)
 * @param sizeAvailability List of currently in-stock sizes (e.g., ["S", "M", "L", "XL"])
 */
data class Product(
    val id: String,
    val name: String,
    val brand: String,
    val price: Int,
    val imageUrl: String = "",
    val category: ProductCategory = ProductCategory.ALL,
    val sizeAvailability: List<String> = listOf("S", "M", "L", "XL"),
    val originalPrice: Int = (price * 1.5).toInt(),
    val discountPercent: Int = if (originalPrice > price) (((originalPrice - price).toFloat() / originalPrice) * 100).toInt() else 0,
    val style: StyleTag = StyleTag.STREETWEAR,
    val colors: List<ProductColor> = listOf(ProductColor("Classic Black", 0xFF18181B)),
    val sizes: List<String> = sizeAvailability,
    val images: List<String> = if (imageUrl.isNotBlank()) listOf(imageUrl) else emptyList(),
    val description: String = "Premium designer streetwear crafted with heavy cotton, drop shoulders, and bespoke detailing.",
    val fabricAndCare: String = "100% Premium Compact Cotton. Machine wash cold, line dry.",
    val deliveryEstimate: String = "⚡ Instant 45m delivery in Bengaluru",
    val rating: Float = 4.8f,
    val reviewsCount: Int = 142,
    val badgeText: String? = null
) {
    /**
     * Resolves the primary image URL whether stored in `imageUrl` or `images`.
     */
    val primaryImage: String
        get() = imageUrl.ifBlank { images.firstOrNull() ?: "" }
}

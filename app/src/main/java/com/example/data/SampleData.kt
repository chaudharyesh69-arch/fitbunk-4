package com.example.data

import com.example.model.*

object SampleData {

    val brands = listOf(
        BrandInfo("VOID STUDIO", "Darkwear Cyberpunk & Heavy Distressed Fits", "Tokyo", "Cyberpunk", "VD", 0xFF6D28D9, 100),
        BrandInfo("NEO-ARCHIVE", "Technical Parachute Utilitarianism & 3D Utility", "Berlin", "Tactical Techwear", "NA", 0xFF3B82F6, 100),
        BrandInfo("CYBER-DRIP", "Y2K Acid Wash, Tour Graphics & Heavy Tees", "Seoul", "Y2K Streetwear", "CD", 0xFFEC4899, 100),
        BrandInfo("KULT", "Oversized Varsity Bombers & Luxury Streetwear", "London", "Luxury Grunge", "KT", 0xFFF59E0B, 100),
        BrandInfo("AESTHETE", "Textured Camp Shirts & Minimal Boxy Fits", "Paris", "Minimal Boxy", "AE", 0xFF10B981, 100),
        BrandInfo("RAW DISTRICT", "Heavyweight Carpenter Denim & Double-Knee Workwear", "New York", "Workwear Street", "RD", 0xFF8B5CF6, 100),
        BrandInfo("OVERKILL CO.", "Heavy French Terry Fleece & Skate Culture", "Los Angeles", "Skate & Fleece", "OK", 0xFFEF4444, 100),
        BrandInfo("BLURRED VISION", "Faded Acid Wash & Melancholy Distressed Knitwear", "Stockholm", "Subtle Grunge", "BV", 0xFF6366F1, 100),
        BrandInfo("ACID HYSTERIA", "Futuristic Rave Wear & Baggy Flared Silhouettes", "Amsterdam", "Rave Streetwear", "AH", 0xFF14B8A6, 100),
        BrandInfo("CHRONO CLAN", "Modular Tech Footwear & Ballistic Crossbody Gear", "Hong Kong", "Futuristic Tech", "CC", 0xFFF97316, 100)
    )

    private val categoryImageMap = mapOf(
        ProductCategory.HOODIES to listOf(
            "https://images.unsplash.com/photo-1556905055-8f358a7a47b2?auto=format&fit=crop&w=600&q=75",
            "https://images.unsplash.com/photo-1509967419530-da38b4704bc6?auto=format&fit=crop&w=600&q=75",
            "https://images.unsplash.com/photo-1578632767115-351597cf2477?auto=format&fit=crop&w=600&q=75"
        ),
        ProductCategory.T_SHIRTS to listOf(
            "https://images.unsplash.com/photo-1521572267360-ee0c2909d518?auto=format&fit=crop&w=600&q=75",
            "https://images.unsplash.com/photo-1503342217505-b0a15ec3261c?auto=format&fit=crop&w=600&q=75",
            "https://images.unsplash.com/photo-1583743814966-8936f5b7be1a?auto=format&fit=crop&w=600&q=75"
        ),
        ProductCategory.TROUSERS to listOf(
            "https://images.unsplash.com/photo-1624378439575-d8705ad7ae80?auto=format&fit=crop&w=600&q=75",
            "https://images.unsplash.com/photo-1517445312882-bc9910d016b7?auto=format&fit=crop&w=600&q=75",
            "https://images.unsplash.com/photo-1473966968600-fa801b869a1a?auto=format&fit=crop&w=600&q=75"
        ),
        ProductCategory.JEANS to listOf(
            "https://images.unsplash.com/photo-1542272604-780c96856592?auto=format&fit=crop&w=600&q=75",
            "https://images.unsplash.com/photo-1582552938357-32b906df40cb?auto=format&fit=crop&w=600&q=75",
            "https://images.unsplash.com/photo-1541099649105-f69ad21f3246?auto=format&fit=crop&w=600&q=75"
        ),
        ProductCategory.JACKETS to listOf(
            "https://images.unsplash.com/photo-1551028719-00167b16eac5?auto=format&fit=crop&w=600&q=75",
            "https://images.unsplash.com/photo-1548883354-7622d03aca27?auto=format&fit=crop&w=600&q=75",
            "https://images.unsplash.com/photo-1544441893-675973e31985?auto=format&fit=crop&w=600&q=75"
        ),
        ProductCategory.SHIRTS to listOf(
            "https://images.unsplash.com/photo-1596755094514-f87e34085b2c?auto=format&fit=crop&w=600&q=75",
            "https://images.unsplash.com/photo-1602810318383-e386cc2a3ccf?auto=format&fit=crop&w=600&q=75",
            "https://images.unsplash.com/photo-1607345366928-199ea26cfe3e?auto=format&fit=crop&w=600&q=75"
        ),
        ProductCategory.SNEAKERS to listOf(
            "https://images.unsplash.com/photo-1552346154-21d32810aba3?auto=format&fit=crop&w=600&q=75",
            "https://images.unsplash.com/photo-1595950653106-6c9ebd614d3a?auto=format&fit=crop&w=600&q=75",
            "https://images.unsplash.com/photo-1549298916-b41d501d3772?auto=format&fit=crop&w=600&q=75"
        ),
        ProductCategory.ACCESSORIES to listOf(
            "https://images.unsplash.com/photo-1553062407-98eeb64c6a62?auto=format&fit=crop&w=600&q=75",
            "https://images.unsplash.com/photo-1622560480605-d83c853bc5c3?auto=format&fit=crop&w=600&q=75",
            "https://images.unsplash.com/photo-1588850561407-ed78c282e89b?auto=format&fit=crop&w=600&q=75"
        )
    )

    private val commonColors = listOf(
        ProductColor("Washed Black", 0xFF1A1A1A),
        ProductColor("Acid Violet", 0xFF6D28D9),
        ProductColor("Lavender Mist", 0xFFB69AFF),
        ProductColor("Matte Slate", 0xFF475569),
        ProductColor("Desert Sand", 0xFFD4C5B9),
        ProductColor("Off White", 0xFFF5F3ED),
        ProductColor("Toxic Green", 0xFF10B981),
        ProductColor("Cyber Neon Pink", 0xFFFF2E7E),
        ProductColor("Vintage Indigo", 0xFF3D5A80),
        ProductColor("Royal Deep Purple", 0xFF210C35)
    )

    private val descriptors = listOf(
        "Heavyweight", "Oversized", "Boxy Fit", "Acid Washed", "Distressed",
        "Tactical 3D", "Vintage Washed", "Drop Shoulder", "Bungee Cinch", "Modular",
        "Raw Hem", "Fleece Lined", "Ripstop", "Double-Knee", "Flared Flap",
        "Embroidered", "Reversible", "Cropped", "Carpenter", "Thermal"
    )

    private val badges = listOf(
        "HOT DROP", "TRENDING", "BESTSELLER", "RESTOCKED", "EXCLUSIVE",
        "LIMITED", "NEW DROP", null, null, null
    )

    private fun generate1000Products(): List<Product> {
        val result = ArrayList<Product>(1000)
        var idCounter = 1

        val categories = listOf(
            ProductCategory.HOODIES,
            ProductCategory.T_SHIRTS,
            ProductCategory.TROUSERS,
            ProductCategory.JEANS,
            ProductCategory.JACKETS,
            ProductCategory.SHIRTS,
            ProductCategory.SNEAKERS,
            ProductCategory.ACCESSORIES
        )

        val styles = listOf(
            StyleTag.STREETWEAR,
            StyleTag.TRENDY,
            StyleTag.MINIMAL,
            StyleTag.CASUAL,
            StyleTag.SPORTY
        )

        // 10 Brands x 100 products = exactly 1,000 distinct items
        brands.forEach { brand ->
            for (i in 1..100) {
                val category = categories[(i - 1) % categories.size]
                val descriptor1 = descriptors[(i * 3 + brand.name.length) % descriptors.size]
                val descriptor2 = descriptors[(i * 7 + 2) % descriptors.size]
                val style = styles[(i + brand.name.hashCode().coerceAtLeast(0)) % styles.size]

                val name = when (category) {
                    ProductCategory.HOODIES -> "$descriptor1 $descriptor2 Fleece Hoodie"
                    ProductCategory.T_SHIRTS -> "$descriptor1 Boxy Graphic Tour Tee"
                    ProductCategory.TROUSERS -> "$descriptor1 Parachute Cargo Trousers"
                    ProductCategory.JEANS -> "$descriptor1 90s Flared Carpenter Denim"
                    ProductCategory.JACKETS -> "$descriptor1 Wool & Leather Bomber Jacket"
                    ProductCategory.SHIRTS -> "$descriptor1 Cuban Revere Collar Shirt"
                    ProductCategory.SNEAKERS -> "$descriptor1 Chunky Platform Runner Kicks"
                    ProductCategory.ACCESSORIES -> "$descriptor1 Modular Ballistic Tech Sling"
                    ProductCategory.ALL -> "$descriptor1 Streetwear Piece"
                }

                val basePrice = when (category) {
                    ProductCategory.T_SHIRTS -> 999 + ((i * 50) % 700)
                    ProductCategory.ACCESSORIES -> 899 + ((i * 60) % 900)
                    ProductCategory.SHIRTS -> 1499 + ((i * 80) % 900)
                    ProductCategory.TROUSERS -> 1999 + ((i * 100) % 1200)
                    ProductCategory.JEANS -> 2299 + ((i * 110) % 1500)
                    ProductCategory.HOODIES -> 2499 + ((i * 120) % 1800)
                    ProductCategory.SNEAKERS -> 3299 + ((i * 150) % 2200)
                    ProductCategory.JACKETS -> 3999 + ((i * 180) % 3000)
                    ProductCategory.ALL -> 1999
                }
                val originalMultiplier = 1.4f + (((i % 5) * 0.1f))
                val originalPrice = (basePrice * originalMultiplier).toInt()

                val productColors = listOf(
                    commonColors[i % commonColors.size],
                    commonColors[(i + 3) % commonColors.size],
                    commonColors[(i + 6) % commonColors.size]
                )

                val sizes = when (category) {
                    ProductCategory.TROUSERS, ProductCategory.JEANS -> listOf("28", "30", "32", "34", "36")
                    ProductCategory.SNEAKERS -> listOf("UK 7", "UK 8", "UK 9", "UK 10", "UK 11")
                    ProductCategory.ACCESSORIES -> listOf("One Size")
                    else -> listOf("S", "M", "L", "XL", "XXL")
                }

                val imagesList = categoryImageMap[category] ?: listOf(
                    "https://images.unsplash.com/photo-1556905055-8f358a7a47b2?auto=format&fit=crop&w=900&q=80"
                )

                val badge = badges[i % badges.size]
                val rating = 4.5f + ((i % 5) * 0.1f)
                val reviews = 40 + (i * 7)

                val description = when (category) {
                    ProductCategory.HOODIES -> "Crafted from 480 GSM French terry cotton with brushed interior, raw distressed detailing, and heavy gauge ribbed cuffs."
                    ProductCategory.T_SHIRTS -> "260 GSM single jersey compact cotton with high-density pigment dye wash, reinforced double-stitched collar, and relaxed drape."
                    ProductCategory.TROUSERS -> "Water-repellent ripstop parachute weave featuring 6 functional 3D cargo pockets and adjustable ankle bungee cinches."
                    ProductCategory.JEANS -> "13.5 oz authentic rigid cotton denim featuring vintage stone wash, reinforced carpenter loops, and flared hem cut."
                    ProductCategory.JACKETS -> "Heavy wool blend torso lined with quilted satin, paired with vegan pebble leather sleeves and custom metallic snap buttons."
                    ProductCategory.SHIRTS -> "Textured linen-cotton blend with an open Cuban revere collar, coconut shell buttons, and a breezy relaxed silhouette."
                    ProductCategory.SNEAKERS -> "Sculptured high-rebound EVA midsole with breathable mesh panels and vegan suede overlays. Delivers cloud-soft cushioning."
                    ProductCategory.ACCESSORIES -> "Heavy 1000D Cordura waterproof ballistic fabric with magnetic Fidlock buckle and waterproof YKK taped zips."
                    ProductCategory.ALL -> "Signature designer streetwear piece with elevated detailing and premium finish."
                }

                val fabric = when (category) {
                    ProductCategory.HOODIES -> "100% French Terry Cotton. Machine wash cold, line dry."
                    ProductCategory.T_SHIRTS -> "100% Combed Compact Cotton. Gentle wash inside out."
                    ProductCategory.TROUSERS -> "100% Ripstop Nylon. Machine wash cold separately."
                    ProductCategory.JEANS -> "100% Heavyweight Cotton Denim. Wash cold, do not tumble dry."
                    ProductCategory.JACKETS -> "70% Wool, 30% Polyester. Dry clean only."
                    ProductCategory.SHIRTS -> "55% Linen, 45% Cotton. Warm iron, machine wash gentle."
                    ProductCategory.SNEAKERS -> "Mesh & Vegan Synthetic Suede. Wipe clean with damp cloth."
                    ProductCategory.ACCESSORIES -> "1000D Cordura Ballistic Nylon. Surface clean only."
                    ProductCategory.ALL -> "Premium blended fibers. Follow label instructions."
                }

                val deliveryMins = 30 + ((i % 4) * 15)

                result.add(
                    Product(
                        id = "fb_${String.format("%04d", idCounter)}",
                        name = "${brand.name} $name",
                        brand = brand.name,
                        price = basePrice,
                        originalPrice = originalPrice,
                        category = category,
                        style = style,
                        colors = productColors,
                        sizes = sizes,
                        images = imagesList,
                        description = description,
                        fabricAndCare = fabric,
                        deliveryEstimate = "⚡ Instant ${deliveryMins}m delivery in Bengaluru",
                        rating = rating,
                        reviewsCount = reviews,
                        badgeText = badge
                    )
                )
                idCounter++
            }
        }
        return result
    }

    val sampleProducts: List<Product> = generate1000Products()
}

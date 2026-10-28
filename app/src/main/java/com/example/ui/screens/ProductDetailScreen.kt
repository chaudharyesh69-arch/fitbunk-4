package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Product
import com.example.model.ProductColor
import com.example.ui.components.FitbunkImage
import com.example.ui.components.FitbunkTopBar
import com.example.ui.theme.*

@Composable
fun ProductDetailScreen(
    product: Product,
    isLiked: Boolean,
    onBackClick: () -> Unit,
    onToggleLike: () -> Unit,
    onAddToCart: (Product, String, ProductColor) -> Unit
) {
    var selectedImageIndex by remember { mutableStateOf(0) }
    var selectedSize by remember { mutableStateOf(product.sizes.firstOrNull() ?: "M") }
    var selectedColor by remember { mutableStateOf(product.colors.firstOrNull() ?: ProductColor("Black", 0xFF000000)) }
    var showSizeGuide by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            FitbunkTopBar(
                title = product.brand,
                canNavigateBack = true,
                onBackClick = onBackClick,
                actions = {
                    IconButton(
                        onClick = onToggleLike,
                        modifier = Modifier.testTag("detail_like_button")
                    ) {
                        Icon(
                            imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Like",
                            tint = if (isLiked) FitbunkAccentPink else FitbunkDeepPurple
                        )
                    }
                }
            )
        },
        bottomBar = {
            // Sticky Add to Cart Bottom Bar
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(12.dp),
                color = FitbunkWhite,
                tonalElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "TOTAL PRICE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = FitbunkTextSecondary
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "₹${product.price}",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black,
                                color = FitbunkDeepPurple
                            )
                            Text(
                                text = "₹${product.originalPrice}",
                                fontSize = 13.sp,
                                color = FitbunkTextSecondary,
                                style = androidx.compose.ui.text.TextStyle(
                                    textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough
                                )
                            )
                        }
                    }

                    Button(
                        onClick = { onAddToCart(product, selectedSize, selectedColor) },
                        modifier = Modifier
                            .height(50.dp)
                            .testTag("detail_add_to_cart_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = FitbunkViolet),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(Icons.Filled.ShoppingBag, contentDescription = null, tint = FitbunkWhite)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("ADD TO CART", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = FitbunkWhite)
                    }
                }
            }
        },
        containerColor = FitbunkLightBg
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Image Gallery
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(360.dp)
            ) {
                val currentImg = product.images.getOrElse(selectedImageIndex) { product.images.first() }
                FitbunkImage(
                    imageUrl = currentImg,
                    contentDescription = product.name,
                    modifier = Modifier.fillMaxSize()
                )

                // Gallery Dots / Thumbnails
                if (product.images.size > 1) {
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        product.images.indices.forEach { index ->
                            Box(
                                modifier = Modifier
                                    .size(if (selectedImageIndex == index) 20.dp else 8.dp, 8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(if (selectedImageIndex == index) FitbunkLavender else FitbunkWhite.copy(alpha = 0.5f))
                                    .clickable { selectedImageIndex = index }
                            )
                        }
                    }
                }
            }

            // Product Information
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Brand, Title & Badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = product.brand,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = FitbunkViolet
                    )

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFD1FAE5)
                    ) {
                        Text(
                            text = "● IN STOCK",
                            color = Color(0xFF047857),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Text(
                    text = product.name,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Black,
                    color = FitbunkDeepPurple
                )

                // Rating & Reviews
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = FitbunkDeepPurple
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Filled.Star, contentDescription = null, tint = FitbunkAccentGold, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("${product.rating}", color = FitbunkWhite, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                    Text("${product.reviewsCount} verified customer ratings", fontSize = 13.sp, color = FitbunkTextSecondary)
                }

                // Delivery Estimate Card
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = FitbunkLavenderLight,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Bolt, contentDescription = null, tint = FitbunkDeepPurple)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(product.deliveryEstimate, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = FitbunkDeepPurple)
                            Text("Micro-hub dispatch within 10 minutes", fontSize = 11.sp, color = FitbunkViolet)
                        }
                    }
                }

                // Select Colour
                Text("Select Colour: ${selectedColor.name}", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = FitbunkDeepPurple)
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    product.colors.forEach { color ->
                        val isSelected = selectedColor.name == color.name
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(color.hex))
                                .border(
                                    width = if (isSelected) 3.dp else 1.dp,
                                    color = if (isSelected) FitbunkViolet else FitbunkCardBorder,
                                    shape = CircleShape
                                )
                                .clickable { selectedColor = color }
                                .testTag("detail_color_${color.name}")
                        )
                    }
                }

                // Select Size & Guide
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Select Size", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = FitbunkDeepPurple)
                    Text(
                        text = "📏 Size Guide",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = FitbunkViolet,
                        modifier = Modifier.clickable { showSizeGuide = true }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    product.sizes.forEach { size ->
                        val isSelected = selectedSize == size
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { selectedSize = size }
                                .testTag("detail_size_$size"),
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) FitbunkDeepPurple else FitbunkWhite,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) FitbunkViolet else FitbunkCardBorder
                            )
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = size,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) FitbunkWhite else FitbunkTextPrimary
                                )
                            }
                        }
                    }
                }

                // Description
                Text("Description & Fit", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = FitbunkDeepPurple)
                Text(
                    text = product.description,
                    fontSize = 14.sp,
                    lineHeight = 22.sp,
                    color = FitbunkTextSecondary
                )

                // Fabric & Care
                Text("Fabric & Care", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = FitbunkDeepPurple)
                Text(
                    text = product.fabricAndCare,
                    fontSize = 14.sp,
                    lineHeight = 22.sp,
                    color = FitbunkTextSecondary
                )

                Spacer(modifier = Modifier.height(20.dp))
            }
        }

        // Size Guide Dialog
        if (showSizeGuide) {
            AlertDialog(
                onDismissRequest = { showSizeGuide = false },
                title = { Text("${product.category.label} Size Guide", fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("• S: Chest 38\" / Waist 30\"", fontSize = 13.sp)
                        Text("• M: Chest 40\" / Waist 32\"", fontSize = 13.sp)
                        Text("• L: Chest 42\" / Waist 34\"", fontSize = 13.sp)
                        Text("• XL: Chest 44\" / Waist 36\"", fontSize = 13.sp)
                        Text("• XXL: Chest 46\" / Waist 38\"", fontSize = 13.sp)
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { showSizeGuide = false },
                        colors = ButtonDefaults.buttonColors(containerColor = FitbunkViolet)
                    ) {
                        Text("Close")
                    }
                }
            )
        }
    }
}

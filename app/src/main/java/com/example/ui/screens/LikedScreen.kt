package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Product
import com.example.model.ProductColor
import com.example.ui.components.FitbunkImage
import com.example.ui.components.FitbunkTopBar
import com.example.ui.theme.*

@Composable
fun LikedScreen(
    likedProducts: List<Product>,
    userDefaultSize: String,
    onProductClick: (Product) -> Unit,
    onRemoveLiked: (Product) -> Unit,
    onAddToCart: (Product, String, ProductColor) -> Unit,
    onStartSwipingClick: () -> Unit
) {
    Scaffold(
        topBar = {
            FitbunkTopBar(title = "Liked Outfits (${likedProducts.size})")
        },
        containerColor = FitbunkLightBg
    ) { padding ->
        if (likedProducts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp)
                    .testTag("liked_empty_state"),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(FitbunkLavenderLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.FavoriteBorder,
                            contentDescription = null,
                            tint = FitbunkViolet,
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "No liked fits yet",
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp,
                        color = FitbunkDeepPurple
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Swipe right on discovery cards or tap the heart icon on any product to save your favorites here.",
                        fontSize = 14.sp,
                        color = FitbunkTextSecondary,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = onStartSwipingClick,
                        colors = ButtonDefaults.buttonColors(containerColor = FitbunkViolet),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.testTag("liked_start_swiping_btn")
                    ) {
                        Icon(Icons.Filled.LocalFireDepartment, contentDescription = null, tint = FitbunkWhite)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("START SWIPING", fontWeight = FontWeight.Bold, color = FitbunkWhite)
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .testTag("liked_products_list"),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(
                    items = likedProducts,
                    key = { it.id },
                    contentType = { "liked_item" }
                ) { product ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .clickable { onProductClick(product) }
                            .testTag("liked_item_${product.id}"),
                        colors = CardDefaults.cardColors(containerColor = FitbunkWhite),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(90.dp)
                                    .clip(RoundedCornerShape(14.dp))
                            ) {
                                FitbunkImage(
                                    imageUrl = product.images.firstOrNull() ?: "",
                                    contentDescription = product.name,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = product.brand,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = FitbunkViolet
                                )

                                Text(
                                    text = product.name,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = FitbunkTextPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = "₹${product.price}",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Black,
                                        color = FitbunkDeepPurple
                                    )
                                    Text(
                                        text = "${product.discountPercent}% OFF",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = FitbunkAccentGreen
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    val sizeToUse = if (product.sizes.contains(userDefaultSize)) {
                                        userDefaultSize
                                    } else {
                                        product.sizes.firstOrNull() ?: "M"
                                    }
                                    val colorToUse = product.colors.firstOrNull() ?: ProductColor("Black", 0xFF000000)

                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = FitbunkLavenderLight
                                    ) {
                                        Text(
                                            text = "Size: $sizeToUse",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = FitbunkDeepPurple,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }

                                    Button(
                                        onClick = { onAddToCart(product, sizeToUse, colorToUse) },
                                        colors = ButtonDefaults.buttonColors(containerColor = FitbunkDeepPurple),
                                        shape = RoundedCornerShape(10.dp),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                        modifier = Modifier.height(32.dp).testTag("liked_add_cart_${product.id}")
                                    ) {
                                        Icon(Icons.Filled.ShoppingBag, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Bag", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            IconButton(
                                onClick = { onRemoveLiked(product) },
                                modifier = Modifier.size(36.dp).testTag("liked_remove_${product.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Remove",
                                    tint = FitbunkTextSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

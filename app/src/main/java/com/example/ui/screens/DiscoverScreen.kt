package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BrandInfo
import com.example.model.Product
import com.example.model.ProductColor
import com.example.ui.components.FitbunkImage
import com.example.ui.components.FitbunkLogo
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun DiscoverScreen(
    products: List<Product>,
    currentIndex: Int,
    cartItemCount: Int,
    canUndo: Boolean,
    brands: List<BrandInfo>,
    selectedBrand: String?,
    onBrandSelect: (String?) -> Unit,
    onSwipeRight: (Product) -> Unit,
    onSwipeLeft: (Product) -> Unit,
    onUndoClick: () -> Unit,
    onResetDeckClick: () -> Unit,
    onProductClick: (Product) -> Unit,
    pendingSizePickerProduct: Product?,
    onConfirmSizePick: (String) -> Unit,
    onDismissSizePick: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val currentProduct = products.getOrNull(currentIndex)
    val nextProduct = products.getOrNull(currentIndex + 1)

    var dragX by remember { mutableFloatStateOf(0f) }
    var dragY by remember { mutableFloatStateOf(0f) }
    val animOffsetX = remember { Animatable(0f) }
    val animOffsetY = remember { Animatable(0f) }
    var isDragging by remember { mutableStateOf(false) }

    // Proactively prefetch the next card's image into Coil memory cache for instant card transitions
    val context = androidx.compose.ui.platform.LocalContext.current
    LaunchedEffect(nextProduct?.id) {
        nextProduct?.images?.firstOrNull()?.let { url ->
            val request = coil.request.ImageRequest.Builder(context)
                .data(url)
                .crossfade(100)
                .build()
            coil.Coil.imageLoader(context).enqueue(request)
        }
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(FitbunkWhite)
                    .shadow(2.dp)
            ) {
                // Header with FITBUNK branding, instant delivery tag, and Undo button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .height(56.dp)
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FitbunkLogo(size = 34.dp)

                        Text(
                            text = "FITBUNK",
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp,
                            letterSpacing = 1.sp,
                            color = FitbunkDeepPurple
                        )

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = FitbunkLavenderLight,
                            modifier = Modifier.padding(start = 4.dp)
                        ) {
                            Text(
                                text = "⚡ 1,000 FITS",
                                color = FitbunkDeepPurple,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }

                    // Undo Button
                    IconButton(
                        onClick = onUndoClick,
                        enabled = canUndo,
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("discover_undo_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Undo Last Swipe",
                            tint = if (canUndo) FitbunkViolet else FitbunkTextSecondary.copy(alpha = 0.4f)
                        )
                    }
                }

                // Brand Selector Horizontal Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // All Brands Chip
                    val isAllSelected = selectedBrand == null || selectedBrand == "All Brands"
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onBrandSelect(null) }
                            .testTag("brand_chip_all"),
                        shape = RoundedCornerShape(12.dp),
                        color = if (isAllSelected) FitbunkDeepPurple else FitbunkLightBg,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isAllSelected) FitbunkViolet else FitbunkCardBorder
                        )
                    ) {
                        Text(
                            text = "✨ All Brands (1,000)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isAllSelected) FitbunkWhite else FitbunkTextPrimary,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }

                    // Individual Brand Chips
                    brands.forEach { brand ->
                        val isSelected = selectedBrand.equals(brand.name, ignoreCase = true)
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { onBrandSelect(brand.name) }
                                .testTag("brand_chip_${brand.name}"),
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) FitbunkViolet else FitbunkWhite,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) FitbunkLavender else FitbunkCardBorder
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(14.dp)
                                        .clip(CircleShape)
                                        .background(Color(brand.accentColor))
                                )
                                Text(
                                    text = "${brand.name} (${brand.totalFits})",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) FitbunkWhite else FitbunkTextPrimary
                                )
                            }
                        }
                    }
                }
            }
        },
        containerColor = FitbunkLightBg
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(14.dp),
            contentAlignment = Alignment.Center
        ) {
            if (currentProduct == null) {
                // Deck finished state
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth().testTag("discover_empty_state")
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(FitbunkLavenderLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = null,
                            tint = FitbunkDeepPurple,
                            modifier = Modifier.size(44.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = if (selectedBrand != null) "All $selectedBrand fits explored!" else "You're all caught up!",
                        fontWeight = FontWeight.Black,
                        fontSize = 22.sp,
                        color = FitbunkDeepPurple
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = if (selectedBrand != null)
                            "You reached the end of $selectedBrand's 100 fits. Switch brands or restart the deck."
                        else
                            "1,000 streetwear pieces are in rotation. Shuffle the deck to discover more fits.",
                        fontSize = 14.sp,
                        color = FitbunkTextSecondary,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 32.dp)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(
                            onClick = onResetDeckClick,
                            colors = ButtonDefaults.buttonColors(containerColor = FitbunkDeepPurple),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.testTag("discover_shuffle_deck_btn")
                        ) {
                            Icon(Icons.Default.Shuffle, contentDescription = null, tint = FitbunkWhite)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("SHUFFLE DECK", fontWeight = FontWeight.Bold, color = FitbunkWhite)
                        }

                        if (selectedBrand != null) {
                            OutlinedButton(
                                onClick = { onBrandSelect(null) },
                                shape = RoundedCornerShape(14.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, FitbunkViolet)
                            ) {
                                Text("VIEW ALL BRANDS", color = FitbunkViolet, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            } else {
                // Next card preview in background (scales up smoothly as top card is swiped)
                if (nextProduct != null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.88f)
                            .graphicsLayer {
                                val currentX = if (isDragging) dragX else animOffsetX.value
                                val progress = (kotlin.math.abs(currentX) / 320f).coerceIn(0f, 1f)
                                scaleX = 0.94f + (0.06f * progress)
                                scaleY = 0.94f + (0.06f * progress)
                                translationY = 32f * (1f - progress)
                            },
                        shape = RoundedCornerShape(26.dp),
                        colors = CardDefaults.cardColors(containerColor = FitbunkWhite),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        FitbunkImage(
                            imageUrl = nextProduct.images.firstOrNull() ?: "",
                            contentDescription = nextProduct.name,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                // Active Swipeable Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.88f)
                        .graphicsLayer {
                            val x = if (isDragging) dragX else animOffsetX.value
                            val y = if (isDragging) dragY else animOffsetY.value
                            translationX = x
                            translationY = y
                            rotationZ = (x / 600f) * 15f
                        }
                        .pointerInput(currentProduct.id) {
                            detectDragGestures(
                                onDragStart = {
                                    isDragging = true
                                },
                                onDragEnd = {
                                    val currentX = dragX
                                    val currentY = dragY
                                    val swipeThreshold = 220f
                                    coroutineScope.launch {
                                        isDragging = false
                                        animOffsetX.snapTo(currentX)
                                        animOffsetY.snapTo(currentY)
                                        dragX = 0f
                                        dragY = 0f

                                        if (currentX > swipeThreshold) {
                                            // Swiped Right -> LIKE / COP
                                            animOffsetX.animateTo(1200f, tween(180))
                                            onSwipeRight(currentProduct)
                                            animOffsetX.snapTo(0f)
                                            animOffsetY.snapTo(0f)
                                        } else if (currentX < -swipeThreshold) {
                                            // Swiped Left -> PASS
                                            animOffsetX.animateTo(-1200f, tween(180))
                                            onSwipeLeft(currentProduct)
                                            animOffsetX.snapTo(0f)
                                            animOffsetY.snapTo(0f)
                                        } else {
                                            // Smooth Spring back
                                            launch { animOffsetX.animateTo(0f, spring(dampingRatio = 0.75f, stiffness = 500f)) }
                                            launch { animOffsetY.animateTo(0f, spring(dampingRatio = 0.75f, stiffness = 500f)) }
                                        }
                                    }
                                },
                                onDragCancel = {
                                    val currentX = dragX
                                    val currentY = dragY
                                    coroutineScope.launch {
                                        isDragging = false
                                        animOffsetX.snapTo(currentX)
                                        animOffsetY.snapTo(currentY)
                                        dragX = 0f
                                        dragY = 0f
                                        launch { animOffsetX.animateTo(0f, spring(dampingRatio = 0.75f, stiffness = 500f)) }
                                        launch { animOffsetY.animateTo(0f, spring(dampingRatio = 0.75f, stiffness = 500f)) }
                                    }
                                },
                                onDrag = { change, dragAmount ->
                                    change.consume()
                                    // Direct float update: ZERO coroutines, instantaneous 120Hz tracking
                                    dragX += dragAmount.x
                                    dragY += dragAmount.y * 0.35f
                                }
                            )
                        }
                        .clickable { onProductClick(currentProduct) }
                        .testTag("discover_swipe_card"),
                    shape = RoundedCornerShape(26.dp),
                    colors = CardDefaults.cardColors(containerColor = FitbunkWhite),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        // Product image
                        FitbunkImage(
                            imageUrl = currentProduct.images.firstOrNull() ?: "",
                            contentDescription = currentProduct.name,
                            modifier = Modifier.fillMaxSize()
                        )

                        // Gradient protection at bottom
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(0.55f)
                                .align(Alignment.BottomCenter)
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            FitbunkDeepPurple.copy(alpha = 0.5f),
                                            FitbunkDeepPurple.copy(alpha = 0.95f)
                                        )
                                    )
                                )
                        )

                        // Swipe Overlays ("COP / LIKE" vs "PASS") - Rendered with graphicsLayer for 0 recompositions
                        Surface(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(24.dp)
                                .graphicsLayer {
                                    val x = if (isDragging) dragX else animOffsetX.value
                                    val alphaProgress = ((x - 40f) / 100f).coerceIn(0f, 1f)
                                    alpha = alphaProgress
                                    scaleX = 0.85f + 0.15f * alphaProgress
                                    scaleY = scaleX
                                    rotationZ = -12f
                                },
                            shape = RoundedCornerShape(10.dp),
                            color = FitbunkViolet.copy(alpha = 0.95f),
                            border = androidx.compose.foundation.BorderStroke(2.dp, FitbunkLavender)
                        ) {
                            Text(
                                text = "COP / LIKE",
                                color = FitbunkWhite,
                                fontWeight = FontWeight.Black,
                                fontSize = 24.sp,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }

                        Surface(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(24.dp)
                                .graphicsLayer {
                                    val x = if (isDragging) dragX else animOffsetX.value
                                    val alphaProgress = ((-x - 40f) / 100f).coerceIn(0f, 1f)
                                    alpha = alphaProgress
                                    scaleX = 0.85f + 0.15f * alphaProgress
                                    scaleY = scaleX
                                    rotationZ = 12f
                                },
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFEF4444).copy(alpha = 0.95f),
                            border = androidx.compose.foundation.BorderStroke(2.dp, Color.White)
                        ) {
                            Text(
                                text = "PASS",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 24.sp,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }

                        // Top Badges
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.TopCenter)
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = FitbunkDeepPurple.copy(alpha = 0.9f)
                            ) {
                                Text(
                                    text = currentProduct.badgeText ?: "FIT #${currentIndex + 1}",
                                    color = FitbunkLavender,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = FitbunkDeepPurple.copy(alpha = 0.85f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Filled.Star, contentDescription = null, tint = FitbunkAccentGold, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${currentProduct.rating}",
                                        color = FitbunkWhite,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        // Bottom Product Card Details
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .fillMaxWidth()
                                .padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = FitbunkViolet
                            ) {
                                Text(
                                    text = currentProduct.brand.uppercase(),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = FitbunkWhite,
                                    letterSpacing = 1.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }

                            Text(
                                text = currentProduct.name,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                color = FitbunkWhite,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )

                            // Price and discount
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "₹${currentProduct.price}",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Black,
                                    color = FitbunkWhite
                                )

                                Text(
                                    text = "₹${currentProduct.originalPrice}",
                                    fontSize = 14.sp,
                                    color = FitbunkLavenderLight.copy(alpha = 0.7f),
                                    style = androidx.compose.ui.text.TextStyle(
                                        textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough
                                    )
                                )

                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = FitbunkAccentGreen
                                ) {
                                    Text(
                                        text = "${currentProduct.discountPercent}% OFF",
                                        color = FitbunkWhite,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            // Available Sizes & Colors
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Sizes chips
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    currentProduct.sizes.take(4).forEach { size ->
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = Color.White.copy(alpha = 0.2f)
                                        ) {
                                            Text(
                                                text = size,
                                                color = FitbunkWhite,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                            )
                                        }
                                    }
                                }

                                // Color dots
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    currentProduct.colors.forEach { color ->
                                        Box(
                                            modifier = Modifier
                                                .size(16.dp)
                                                .clip(CircleShape)
                                                .background(Color(color.hex))
                                                .border(1.5.dp, Color.White, CircleShape)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Dislike and Like action buttons floating at bottom of card
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(36.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Dislike / Pass Button
                    IconButton(
                        onClick = {
                            coroutineScope.launch {
                                isDragging = false
                                animOffsetX.snapTo(0f)
                                animOffsetY.snapTo(0f)
                                animOffsetX.animateTo(-1000f, tween(180))
                                onSwipeLeft(currentProduct)
                                animOffsetX.snapTo(0f)
                                animOffsetY.snapTo(0f)
                            }
                        },
                        modifier = Modifier
                            .size(64.dp)
                            .shadow(8.dp, CircleShape)
                            .clip(CircleShape)
                            .background(FitbunkWhite)
                            .border(1.dp, FitbunkCardBorder, CircleShape)
                            .testTag("discover_pass_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Pass",
                            tint = Color(0xFFEF4444),
                            modifier = Modifier.size(30.dp)
                        )
                    }

                    // Like / Cop Button
                    IconButton(
                        onClick = {
                            coroutineScope.launch {
                                isDragging = false
                                animOffsetX.snapTo(0f)
                                animOffsetY.snapTo(0f)
                                animOffsetX.animateTo(1000f, tween(180))
                                onSwipeRight(currentProduct)
                                animOffsetX.snapTo(0f)
                                animOffsetY.snapTo(0f)
                            }
                        },
                        modifier = Modifier
                            .size(72.dp)
                            .shadow(12.dp, CircleShape)
                            .clip(CircleShape)
                            .background(FitbunkViolet)
                            .border(2.dp, FitbunkLavender, CircleShape)
                            .testTag("discover_like_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ShoppingBag,
                            contentDescription = "Like & Cart",
                            tint = FitbunkWhite,
                            modifier = Modifier.size(34.dp)
                        )
                    }
                }
            }
        }

        // Compact Size Selection Popup (when user likes a product but size is unknown)
        if (pendingSizePickerProduct != null) {
            AlertDialog(
                onDismissRequest = onDismissSizePick,
                title = {
                    Text(
                        text = "Pick your size",
                        fontWeight = FontWeight.Black,
                        color = FitbunkDeepPurple
                    )
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = "Select size for ${pendingSizePickerProduct.name} to add to cart:",
                            fontSize = 13.sp,
                            color = FitbunkTextSecondary
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            pendingSizePickerProduct.sizes.forEach { size ->
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(44.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { onConfirmSizePick(size) }
                                        .testTag("size_pick_$size"),
                                    shape = RoundedCornerShape(8.dp),
                                    color = FitbunkLavenderLight,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, FitbunkViolet)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = size,
                                            fontWeight = FontWeight.Bold,
                                            color = FitbunkDeepPurple
                                        )
                                    }
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = onDismissSizePick) {
                        Text("Cancel", color = FitbunkTextSecondary)
                    }
                }
            )
        }
    }
}

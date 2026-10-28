package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import com.example.R
import coil.compose.AsyncImage
import com.example.model.Product
import com.example.navigation.Screen
import com.example.ui.theme.*

@Composable
fun FitbunkLogo(
    modifier: Modifier = Modifier,
    size: Dp = 36.dp
) {
    Image(
        painter = painterResource(id = R.drawable.fitbunk_logo),
        contentDescription = "FITBUNK Official Brand Logo",
        modifier = modifier
            .size(size)
            .clip(CircleShape),
        contentScale = ContentScale.Crop
    )
}

@Composable
fun FitbunkTopBar(
    title: String,
    canNavigateBack: Boolean = false,
    onBackClick: () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {}
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = FitbunkWhite,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .height(60.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (canNavigateBack) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .size(40.dp)
                            .testTag("top_bar_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = FitbunkDeepPurple
                        )
                    }
                } else {
                    FitbunkLogo(size = 36.dp)
                }

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = FitbunkDeepPurple
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                content = actions
            )
        }
    }
}

@Composable
fun FitbunkBottomNav(
    currentTab: Screen,
    cartItemCount: Int,
    likedItemCount: Int,
    onTabSelected: (Screen) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(16.dp),
        color = FitbunkWhite,
        tonalElevation = 6.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .height(68.dp)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            BottomNavItem(
                label = "Discover",
                icon = Icons.Outlined.LocalFireDepartment,
                selectedIcon = Icons.Filled.LocalFireDepartment,
                isSelected = currentTab is Screen.Discover,
                testTag = "nav_discover",
                onClick = { onTabSelected(Screen.Discover) }
            )

            BottomNavItem(
                label = "Search",
                icon = Icons.Outlined.Search,
                selectedIcon = Icons.Filled.Search,
                isSelected = currentTab is Screen.Search,
                testTag = "nav_search",
                onClick = { onTabSelected(Screen.Search) }
            )

            BottomNavItem(
                label = "Liked",
                icon = Icons.Outlined.FavoriteBorder,
                selectedIcon = Icons.Filled.Favorite,
                isSelected = currentTab is Screen.Liked,
                badgeCount = likedItemCount,
                testTag = "nav_liked",
                onClick = { onTabSelected(Screen.Liked) }
            )

            BottomNavItem(
                label = "Cart",
                icon = Icons.Outlined.ShoppingBag,
                selectedIcon = Icons.Filled.ShoppingBag,
                isSelected = currentTab is Screen.Cart,
                badgeCount = cartItemCount,
                testTag = "nav_cart",
                onClick = { onTabSelected(Screen.Cart) }
            )

            BottomNavItem(
                label = "Profile",
                icon = Icons.Outlined.Person,
                selectedIcon = Icons.Filled.Person,
                isSelected = currentTab is Screen.Profile,
                testTag = "nav_profile",
                onClick = { onTabSelected(Screen.Profile) }
            )
        }
    }
}

@Composable
private fun BottomNavItem(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    badgeCount: Int = 0,
    testTag: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .testTag(testTag),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        BadgedBox(
            badge = {
                if (badgeCount > 0) {
                    Badge(
                        containerColor = FitbunkViolet,
                        contentColor = FitbunkWhite
                    ) {
                        Text(
                            text = if (badgeCount > 9) "9+" else badgeCount.toString(),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        ) {
            Icon(
                imageVector = if (isSelected) selectedIcon else icon,
                contentDescription = label,
                tint = if (isSelected) FitbunkViolet else FitbunkTextSecondary,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) FitbunkViolet else FitbunkTextSecondary
        )
    }
}

@Composable
fun FitbunkImage(
    imageUrl: String,
    contentDescription: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val imageRequest = remember(imageUrl) {
        coil.request.ImageRequest.Builder(context)
            .data(imageUrl)
            .crossfade(150)
            .build()
    }

    Box(
        modifier = modifier
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFF2E1349), Color(0xFF4C2077))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = imageRequest,
            contentDescription = contentDescription,
            modifier = Modifier.fillMaxSize(),
            contentScale = contentScale
        )
    }
}

@Composable
fun ProductGridCard(
    product: Product,
    isLiked: Boolean,
    onCardClick: () -> Unit,
    onLikeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onCardClick)
            .testTag("product_grid_card_${product.id}"),
        colors = CardDefaults.cardColors(containerColor = FitbunkWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                FitbunkImage(
                    imageUrl = product.images.firstOrNull() ?: "",
                    contentDescription = product.name,
                    modifier = Modifier.fillMaxSize()
                )

                // Like Button
                IconButton(
                    onClick = onLikeClick,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(FitbunkWhite.copy(alpha = 0.85f))
                        .testTag("like_btn_${product.id}")
                ) {
                    Icon(
                        imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Like",
                        tint = if (isLiked) FitbunkAccentPink else FitbunkDeepPurple,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Discount or Badge tag
                if (product.badgeText != null) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp),
                        shape = RoundedCornerShape(6.dp),
                        color = FitbunkDeepPurple
                    ) {
                        Text(
                            text = product.badgeText,
                            color = FitbunkLavender,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = product.brand,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = FitbunkViolet
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = product.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = FitbunkTextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "₹${product.price}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        color = FitbunkDeepPurple
                    )

                    Text(
                        text = "₹${product.originalPrice}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        color = FitbunkTextSecondary,
                        style = androidx.compose.ui.text.TextStyle(
                            textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough
                        )
                    )

                    Text(
                        text = "${product.discountPercent}% OFF",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = FitbunkAccentGreen
                    )
                }
            }
        }
    }
}

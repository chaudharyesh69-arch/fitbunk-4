package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BrandInfo
import com.example.model.Product
import com.example.model.ProductCategory
import com.example.model.StyleTag
import com.example.ui.components.FitbunkTopBar
import com.example.ui.components.ProductGridCard
import com.example.ui.theme.*

@Composable
fun SearchScreen(
    products: List<Product>,
    likedProductIds: Set<String>,
    brands: List<BrandInfo>,
    selectedBrand: String?,
    searchQuery: String,
    selectedCategory: ProductCategory,
    selectedStyle: StyleTag?,
    selectedSort: String,
    onBrandChange: (String?) -> Unit,
    onQueryChange: (String) -> Unit,
    onCategoryChange: (ProductCategory) -> Unit,
    onStyleChange: (StyleTag?) -> Unit,
    onSortChange: (String) -> Unit,
    onProductClick: (Product) -> Unit,
    onToggleLike: (Product) -> Unit
) {
    val trendingSearches = listOf("Oversized Tees", "Cargo Pants", "Varsity Bomber", "Platform Sneakers", "Acid Wash")
    val sortOptions = listOf("Recommended", "Price: Low to High", "Price: High to Low", "Rating")

    Scaffold(
        topBar = {
            FitbunkTopBar(title = "Explore Catalog (${products.size})")
        },
        containerColor = FitbunkLightBg
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Search Input
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onQueryChange,
                placeholder = { Text("Search across 1,000 fits...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = FitbunkViolet) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onQueryChange("") }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear search", tint = FitbunkTextSecondary)
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = FitbunkWhite,
                    unfocusedContainerColor = FitbunkWhite,
                    focusedBorderColor = FitbunkViolet,
                    unfocusedBorderColor = FitbunkCardBorder
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .testTag("search_text_input")
            )

            // Shop by Brand Carousel Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🏷️ SELECT BRAND INVENTORY",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = FitbunkDeepPurple,
                    letterSpacing = 0.5.sp
                )

                if (selectedBrand != null) {
                    Text(
                        text = "Clear Brand (All)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = FitbunkViolet,
                        modifier = Modifier.clickable { onBrandChange(null) }
                    )
                }
            }

            // Brands Horizontal Scroll
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // All Brands option
                val isAll = selectedBrand == null
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onBrandChange(null) }
                        .testTag("search_brand_all"),
                    shape = RoundedCornerShape(12.dp),
                    color = if (isAll) FitbunkDeepPurple else FitbunkWhite,
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isAll) FitbunkViolet else FitbunkCardBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "All Brands (1,000)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = if (isAll) FitbunkWhite else FitbunkTextPrimary
                        )
                    }
                }

                // Individual Brand Cards
                brands.forEach { brand ->
                    val isSelected = selectedBrand.equals(brand.name, ignoreCase = true)
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onBrandChange(brand.name) }
                            .testTag("search_brand_${brand.name}"),
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) FitbunkViolet else FitbunkWhite,
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) FitbunkLavender else FitbunkCardBorder)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(22.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) FitbunkWhite else Color(brand.accentColor)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = brand.logoText,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (isSelected) FitbunkViolet else FitbunkWhite
                                )
                            }

                            Column {
                                Text(
                                    text = brand.name,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) FitbunkWhite else FitbunkTextPrimary
                                )
                                Text(
                                    text = "${brand.totalFits} fits • ${brand.origin}",
                                    fontSize = 10.sp,
                                    color = if (isSelected) FitbunkLavenderLight else FitbunkTextSecondary
                                )
                            }
                        }
                    }
                }
            }

            // Categories horizontal bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ProductCategory.values().forEach { category ->
                    val isSelected = selectedCategory == category
                    FilterChip(
                        selected = isSelected,
                        onClick = { onCategoryChange(category) },
                        label = { Text(category.label, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = FitbunkDeepPurple,
                            selectedLabelColor = FitbunkWhite,
                            containerColor = FitbunkWhite
                        ),
                        modifier = Modifier.testTag("cat_filter_${category.name}")
                    )
                }
            }

            // Sort & Results count
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${products.size} fits available",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = FitbunkTextSecondary
                )

                // Sort dropdown
                var sortMenuExpanded by remember { mutableStateOf(false) }
                Box {
                    TextButton(
                        onClick = { sortMenuExpanded = true },
                        modifier = Modifier.testTag("sort_selector_btn")
                    ) {
                        Text("Sort: $selectedSort", color = FitbunkViolet, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = FitbunkViolet)
                    }

                    DropdownMenu(
                        expanded = sortMenuExpanded,
                        onDismissRequest = { sortMenuExpanded = false }
                    ) {
                        sortOptions.forEach { sort ->
                            DropdownMenuItem(
                                text = { Text(sort) },
                                onClick = {
                                    onSortChange(sort)
                                    sortMenuExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            // Product Grid
            if (products.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.SearchOff,
                            contentDescription = null,
                            tint = FitbunkLavender,
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No fits found in this filter",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = FitbunkDeepPurple
                        )
                        Text(
                            text = "Try clearing the brand or category filter.",
                            color = FitbunkTextSecondary,
                            fontSize = 13.sp
                        )
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp)
                        .testTag("search_product_grid"),
                    contentPadding = PaddingValues(bottom = 80.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(
                        items = products,
                        key = { it.id },
                        contentType = { "product_card" }
                    ) { product ->
                        ProductGridCard(
                            product = product,
                            isLiked = likedProductIds.contains(product.id),
                            onCardClick = { onProductClick(product) },
                            onLikeClick = { onToggleLike(product) }
                        )
                    }
                }
            }
        }
    }
}

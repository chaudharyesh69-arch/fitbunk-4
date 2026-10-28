package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.components.FitbunkTopBar
import com.example.ui.theme.*

// ==========================================
// SCREEN 4: BASIC PROFILE SETUP
// ==========================================
@Composable
fun ProfileSetupScreen(
    currentProfile: UserProfile,
    onSaveAndContinue: (name: String, ageConfirmed: Boolean, categories: Set<ProductCategory>, style: StyleTag, budget: String) -> Unit,
    onSkip: () -> Unit
) {
    var name by remember { mutableStateOf(currentProfile.name) }
    var ageConfirmed by remember { mutableStateOf(currentProfile.ageConfirmed) }
    var selectedCategories by remember { mutableStateOf(currentProfile.preferredCategories) }
    var selectedStyle by remember { mutableStateOf(currentProfile.preferredStyle) }
    var selectedBudget by remember { mutableStateOf(currentProfile.budgetPreference) }

    val budgetOptions = listOf("₹500 - ₹1500", "₹1500 - ₹3500", "₹3500+")

    Scaffold(
        topBar = {
            FitbunkTopBar(
                title = "Profile Setup (1/3)",
                actions = {
                    TextButton(onClick = onSkip, modifier = Modifier.testTag("profile_setup_skip")) {
                        Text("Skip", color = FitbunkViolet, fontWeight = FontWeight.Bold)
                    }
                }
            )
        },
        containerColor = FitbunkLightBg
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header
            Text(
                text = "Personalize your fit stream",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Black,
                color = FitbunkDeepPurple
            )
            Text(
                text = "We curate outfits based on your vibe, favorite pieces, and budget.",
                color = FitbunkTextSecondary,
                fontSize = 14.sp
            )

            // Name Input
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Display Name") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("setup_name_input"),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = FitbunkWhite,
                    unfocusedContainerColor = FitbunkWhite,
                    focusedBorderColor = FitbunkViolet
                )
            )

            // Age Confirmation
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = FitbunkWhite,
                border = androidx.compose.foundation.BorderStroke(1.dp, FitbunkCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("18+ Age Confirmation", fontWeight = FontWeight.Bold, color = FitbunkDeepPurple)
                        Text("Required for adult streetwear sizing and checkout", fontSize = 12.sp, color = FitbunkTextSecondary)
                    }
                    Switch(
                        checked = ageConfirmed,
                        onCheckedChange = { ageConfirmed = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = FitbunkWhite,
                            checkedTrackColor = FitbunkViolet
                        ),
                        modifier = Modifier.testTag("setup_age_switch")
                    )
                }
            }

            // Style Preference Cards
            Text(
                text = "What's your primary aesthetic?",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = FitbunkDeepPurple
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StyleTag.values().take(3).forEach { style ->
                    val isSelected = selectedStyle == style
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { selectedStyle = style }
                            .testTag("style_${style.name}"),
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) FitbunkDeepPurple else FitbunkWhite,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) FitbunkViolet else FitbunkCardBorder
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(style.iconText, fontSize = 20.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = style.label,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) FitbunkWhite else FitbunkTextPrimary
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StyleTag.values().drop(3).forEach { style ->
                    val isSelected = selectedStyle == style
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { selectedStyle = style }
                            .testTag("style_${style.name}"),
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) FitbunkDeepPurple else FitbunkWhite,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) FitbunkViolet else FitbunkCardBorder
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(style.iconText, fontSize = 20.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = style.label,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) FitbunkWhite else FitbunkTextPrimary
                            )
                        }
                    }
                }
            }

            // Categories
            Text(
                text = "Select fits you frequently cop:",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = FitbunkDeepPurple
            )

            val availableCategories = listOf(
                ProductCategory.HOODIES,
                ProductCategory.T_SHIRTS,
                ProductCategory.TROUSERS,
                ProductCategory.JEANS,
                ProductCategory.JACKETS,
                ProductCategory.SNEAKERS,
                ProductCategory.ACCESSORIES
            )

            // Category Chips
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                availableCategories.chunked(3).forEach { rowCats ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowCats.forEach { cat ->
                            val isSelected = selectedCategories.contains(cat)
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    selectedCategories = if (isSelected) {
                                        selectedCategories - cat
                                    } else {
                                        selectedCategories + cat
                                    }
                                },
                                label = { Text(cat.label, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = FitbunkViolet,
                                    selectedLabelColor = FitbunkWhite,
                                    containerColor = FitbunkWhite
                                ),
                                modifier = Modifier.weight(1f).testTag("cat_chip_${cat.name}")
                            )
                        }
                    }
                }
            }

            // Budget Preference
            Text(
                text = "Typical outfit budget:",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = FitbunkDeepPurple
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                budgetOptions.forEach { budget ->
                    val isSelected = selectedBudget == budget
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { selectedBudget = budget }
                            .testTag("budget_$budget"),
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) FitbunkViolet else FitbunkWhite,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) FitbunkViolet else FitbunkCardBorder
                        )
                    ) {
                        Text(
                            text = budget,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) FitbunkWhite else FitbunkTextPrimary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Continue Button
            Button(
                onClick = {
                    onSaveAndContinue(name, ageConfirmed, selectedCategories, selectedStyle, selectedBudget)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("setup_continue_button"),
                colors = ButtonDefaults.buttonColors(containerColor = FitbunkDeepPurple),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("NEXT: SIZE PREFERENCES", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = FitbunkWhite)
            }
        }
    }
}

// ==========================================
// SCREEN 5: SIZE PREFERENCES
// ==========================================
@Composable
fun SizePreferencesScreen(
    currentProfile: UserProfile,
    onSaveAndContinue: (tops: String, bottoms: String, shoes: String) -> Unit,
    onSkip: () -> Unit
) {
    var topSize by remember { mutableStateOf(currentProfile.sizeTops.ifBlank { "L" }) }
    var bottomSize by remember { mutableStateOf(currentProfile.sizeBottoms.ifBlank { "32" }) }
    var shoeSize by remember { mutableStateOf(currentProfile.sizeShoes.ifBlank { "UK 9" }) }
    var showSizeGuide by remember { mutableStateOf(false) }

    val topOptions = listOf("XS", "S", "M", "L", "XL", "XXL")
    val bottomOptions = listOf("28", "30", "32", "34", "36", "38")
    val shoeOptions = listOf("UK 6", "UK 7", "UK 8", "UK 9", "UK 10", "UK 11")

    Scaffold(
        topBar = {
            FitbunkTopBar(
                title = "Size Preferences (2/3)",
                actions = {
                    TextButton(onClick = onSkip, modifier = Modifier.testTag("size_pref_skip")) {
                        Text("Skip", color = FitbunkViolet, fontWeight = FontWeight.Bold)
                    }
                }
            )
        },
        containerColor = FitbunkLightBg
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(22.dp)
        ) {
            Text(
                text = "Select your fit sizes",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Black,
                color = FitbunkDeepPurple
            )

            // Info Card about variation
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = FitbunkLavenderLight,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = FitbunkDeepPurple)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Sizes vary between streetwear cuts (oversized, boxy, slim). You can easily fine-tune size before or after swiping.",
                        fontSize = 12.sp,
                        color = FitbunkDeepPurple,
                        lineHeight = 17.sp
                    )
                }
            }

            // Size Guide Link
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Tops & Outerwear", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = FitbunkDeepPurple)
                Text(
                    text = "📏 Size Guide",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = FitbunkViolet,
                    modifier = Modifier
                        .clickable { showSizeGuide = true }
                        .testTag("open_size_guide_btn")
                )
            }

            // Tops grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                topOptions.forEach { size ->
                    val isSelected = topSize == size
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { topSize = size }
                            .testTag("size_top_$size"),
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) FitbunkDeepPurple else FitbunkWhite,
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) FitbunkViolet else FitbunkCardBorder)
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

            // Bottoms (Waist in inches)
            Text("Bottoms & Trousers (Waist)", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = FitbunkDeepPurple)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                bottomOptions.forEach { size ->
                    val isSelected = bottomSize == size
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { bottomSize = size }
                            .testTag("size_bottom_$size"),
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) FitbunkDeepPurple else FitbunkWhite,
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) FitbunkViolet else FitbunkCardBorder)
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

            // Shoes (UK sizing)
            Text("Sneakers & Footwear (UK Sizing)", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = FitbunkDeepPurple)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                shoeOptions.forEach { size ->
                    val isSelected = shoeSize == size
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { shoeSize = size }
                            .testTag("size_shoe_$size"),
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) FitbunkDeepPurple else FitbunkWhite,
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) FitbunkViolet else FitbunkCardBorder)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = size.replace("UK ", ""),
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) FitbunkWhite else FitbunkTextPrimary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Continue Button
            Button(
                onClick = { onSaveAndContinue(topSize, bottomSize, shoeSize) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("size_continue_btn"),
                colors = ButtonDefaults.buttonColors(containerColor = FitbunkDeepPurple),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("NEXT: DELIVERY LOCATION", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = FitbunkWhite)
            }
        }

        // Size Guide Dialog
        if (showSizeGuide) {
            AlertDialog(
                onDismissRequest = { showSizeGuide = false },
                title = { Text("FITBUNK Size Guide", fontWeight = FontWeight.Black) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("• Oversized Tees & Hoodies: Cut 1.5x wider for true Gen-Z drop shoulders.", fontSize = 13.sp)
                        Text("• Boxy Fit: Slightly cropped body with wide chest circumference.", fontSize = 13.sp)
                        Text("• Parachute & Cargo: Equipped with bungee cinches for customizable length.", fontSize = 13.sp)
                        Text("• Sneakers: Fits true to standard UK / Nike sizing.", fontSize = 13.sp)
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { showSizeGuide = false },
                        colors = ButtonDefaults.buttonColors(containerColor = FitbunkViolet)
                    ) {
                        Text("Got it")
                    }
                }
            )
        }
    }
}

// ==========================================
// SCREEN 6: LOCATION AND DELIVERY
// ==========================================
@Composable
fun LocationSetupScreen(
    currentAddresses: List<DeliveryAddress>,
    onSaveAddressAndFinish: (DeliveryAddress) -> Unit,
    onSkip: () -> Unit
) {
    var houseFlat by remember { mutableStateOf("Flat 402, Skyline Residency") }
    var street by remember { mutableStateOf("12th Main, 4th Block") }
    var area by remember { mutableStateOf("Koramangala") }
    var city by remember { mutableStateOf("Bengaluru") }
    var state by remember { mutableStateOf("Karnataka") }
    var pinCode by remember { mutableStateOf("560034") }
    var addressLabel by remember { mutableStateOf("Home") }

    var validationError by remember { mutableStateOf<String?>(null) }
    var isCheckingServiceability by remember { mutableStateOf(false) }
    var serviceabilityChecked by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            FitbunkTopBar(
                title = "Delivery Location (3/3)",
                actions = {
                    TextButton(onClick = onSkip, modifier = Modifier.testTag("location_skip_btn")) {
                        Text("Skip", color = FitbunkViolet, fontWeight = FontWeight.Bold)
                    }
                }
            )
        },
        containerColor = FitbunkLightBg
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Where should we drop your fits?",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Black,
                color = FitbunkDeepPurple
            )

            // Why location is needed explanation
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = FitbunkWhite,
                border = androidx.compose.foundation.BorderStroke(1.dp, FitbunkCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.ElectricBolt, contentDescription = null, tint = FitbunkAccentGold)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "FITBUNK operates hyperfast micro-fulfilment dark stores. We verify PIN code serviceability to ensure instant 60-minute delivery to your doorstep.",
                        fontSize = 12.sp,
                        color = FitbunkDeepPurple
                    )
                }
            }

            // Real Serviceability Badge
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFFD1FAE5),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = FitbunkAccentGreen, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "⚡ Instant 60-min delivery active",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF065F46),
                            fontSize = 13.sp
                        )
                        Text(
                            text = "Serviceable in $city ($pinCode)",
                            fontSize = 11.sp,
                            color = Color(0xFF047857)
                        )
                    }
                }
            }

            // Address Label Chips
            Text("Address Type:", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = FitbunkDeepPurple)
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                listOf("Home", "Work", "Campus").forEach { label ->
                    val isSelected = addressLabel == label
                    FilterChip(
                        selected = isSelected,
                        onClick = { addressLabel = label },
                        label = { Text(label) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = FitbunkDeepPurple,
                            selectedLabelColor = FitbunkWhite
                        ),
                        modifier = Modifier.testTag("addr_type_$label")
                    )
                }
            }

            // Form Inputs
            OutlinedTextField(
                value = houseFlat,
                onValueChange = { houseFlat = it; validationError = null },
                label = { Text("Flat / House / Apartment No.") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("input_house_flat"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = FitbunkWhite, unfocusedContainerColor = FitbunkWhite)
            )

            OutlinedTextField(
                value = street,
                onValueChange = { street = it; validationError = null },
                label = { Text("Street / Road / Landmark") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("input_street"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = FitbunkWhite, unfocusedContainerColor = FitbunkWhite)
            )

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = area,
                    onValueChange = { area = it; validationError = null },
                    label = { Text("Area / Locality") },
                    singleLine = true,
                    modifier = Modifier.weight(1f).testTag("input_area"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = FitbunkWhite, unfocusedContainerColor = FitbunkWhite)
                )

                OutlinedTextField(
                    value = pinCode,
                    onValueChange = {
                        if (it.length <= 6 && it.all { char -> char.isDigit() }) {
                            pinCode = it
                            validationError = null
                        }
                    },
                    label = { Text("PIN Code") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f).testTag("input_pincode"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = FitbunkWhite, unfocusedContainerColor = FitbunkWhite)
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = city,
                    onValueChange = { city = it; validationError = null },
                    label = { Text("City") },
                    singleLine = true,
                    modifier = Modifier.weight(1f).testTag("input_city"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = FitbunkWhite, unfocusedContainerColor = FitbunkWhite)
                )

                OutlinedTextField(
                    value = state,
                    onValueChange = { state = it; validationError = null },
                    label = { Text("State") },
                    singleLine = true,
                    modifier = Modifier.weight(1f).testTag("input_state"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = FitbunkWhite, unfocusedContainerColor = FitbunkWhite)
                )
            }

            // Validation Error
            if (validationError != null) {
                Text(
                    text = validationError ?: "",
                    color = FitbunkAccentPink,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Save & Start Browsing
            Button(
                onClick = {
                    if (houseFlat.isBlank() || street.isBlank() || pinCode.length != 6) {
                        validationError = "Please enter valid flat, street and 6-digit PIN code."
                        return@Button
                    }

                    val newAddr = DeliveryAddress(
                        id = "addr_${System.currentTimeMillis()}",
                        label = addressLabel,
                        houseFlat = houseFlat,
                        street = street,
                        area = area,
                        city = city,
                        state = state,
                        pinCode = pinCode,
                        isDefault = true
                    )
                    onSaveAddressAndFinish(newAddr)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("save_location_button"),
                colors = ButtonDefaults.buttonColors(containerColor = FitbunkViolet),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("START DISCOVERING FITS ⚡", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = FitbunkWhite)
            }
        }
    }
}

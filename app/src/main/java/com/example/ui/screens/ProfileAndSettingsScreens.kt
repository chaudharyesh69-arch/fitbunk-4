package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.components.FitbunkTopBar
import com.example.ui.theme.*

// ==========================================
// SCREEN 15: PROFILE
// ==========================================
@Composable
fun ProfileScreen(
    userProfile: UserProfile,
    likedCount: Int,
    orders: List<Order>,
    onOpenOrderTracking: (Order) -> Unit,
    onOpenSettings: () -> Unit,
    onOpenLiked: () -> Unit,
    onUpdateProfile: (name: String, email: String) -> Unit,
    onUpdateSizes: (tops: String, bottoms: String, shoes: String) -> Unit,
    onLogout: () -> Unit
) {
    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showEditSizesDialog by remember { mutableStateOf(false) }
    var editName by remember { mutableStateOf(userProfile.name) }
    var editEmail by remember { mutableStateOf(userProfile.email) }

    Scaffold(
        topBar = {
            FitbunkTopBar(
                title = "My Profile",
                actions = {
                    IconButton(onClick = onOpenSettings, modifier = Modifier.testTag("profile_settings_btn")) {
                        Icon(Icons.Outlined.Settings, contentDescription = "Settings", tint = FitbunkDeepPurple)
                    }
                }
            )
        },
        containerColor = FitbunkLightBg
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .testTag("profile_screen_scroll"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Profile Header
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("profile_header_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = FitbunkWhite),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(FitbunkDeepPurple),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = userProfile.name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString(""),
                                color = FitbunkLavender,
                                fontWeight = FontWeight.Black,
                                fontSize = 24.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = userProfile.name,
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp,
                            color = FitbunkDeepPurple
                        )

                        Text(
                            text = userProfile.email,
                            fontSize = 13.sp,
                            color = FitbunkTextSecondary
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Surface(shape = RoundedCornerShape(12.dp), color = FitbunkLavenderLight) {
                            Text(
                                text = "VIP FITBUNK CLUB MEMBER ⚡",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = FitbunkDeepPurple,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedButton(
                            onClick = {
                                editName = userProfile.name
                                editEmail = userProfile.email
                                showEditProfileDialog = true
                            },
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, FitbunkViolet),
                            modifier = Modifier.testTag("edit_profile_btn")
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, tint = FitbunkViolet, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Edit Details", color = FitbunkViolet, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Quick Stats Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Liked Outfits
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .clickable(onClick = onOpenLiked)
                            .testTag("stat_liked_fits"),
                        shape = RoundedCornerShape(16.dp),
                        color = FitbunkWhite,
                        border = androidx.compose.foundation.BorderStroke(1.dp, FitbunkCardBorder)
                    ) {
                        Column(modifier = Modifier.padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("$likedCount", fontSize = 22.sp, fontWeight = FontWeight.Black, color = FitbunkViolet)
                            Text("Liked Fits", fontSize = 12.sp, color = FitbunkTextSecondary, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    // Orders Placed
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        color = FitbunkWhite,
                        border = androidx.compose.foundation.BorderStroke(1.dp, FitbunkCardBorder)
                    ) {
                        Column(modifier = Modifier.padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("${orders.size}", fontSize = 22.sp, fontWeight = FontWeight.Black, color = FitbunkDeepPurple)
                            Text("Orders", fontSize = 12.sp, color = FitbunkTextSecondary, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    // Saved Addresses
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        color = FitbunkWhite,
                        border = androidx.compose.foundation.BorderStroke(1.dp, FitbunkCardBorder)
                    ) {
                        Column(modifier = Modifier.padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("${userProfile.addresses.size}", fontSize = 22.sp, fontWeight = FontWeight.Black, color = FitbunkAccentGreen)
                            Text("Addresses", fontSize = 12.sp, color = FitbunkTextSecondary, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            // Size Preferences Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("profile_size_preferences_card"),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = FitbunkWhite),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("My Fit Sizes", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = FitbunkDeepPurple)
                            Text(
                                text = "Change",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = FitbunkViolet,
                                modifier = Modifier.clickable { showEditSizesDialog = true }
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(shape = RoundedCornerShape(10.dp), color = FitbunkLavenderLight, modifier = Modifier.weight(1f)) {
                                Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("TOPS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = FitbunkViolet)
                                    Text(userProfile.sizeTops.ifBlank { "L" }, fontSize = 16.sp, fontWeight = FontWeight.Black, color = FitbunkDeepPurple)
                                }
                            }

                            Surface(shape = RoundedCornerShape(10.dp), color = FitbunkLavenderLight, modifier = Modifier.weight(1f)) {
                                Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("BOTTOMS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = FitbunkViolet)
                                    Text(userProfile.sizeBottoms.ifBlank { "32" }, fontSize = 16.sp, fontWeight = FontWeight.Black, color = FitbunkDeepPurple)
                                }
                            }

                            Surface(shape = RoundedCornerShape(10.dp), color = FitbunkLavenderLight, modifier = Modifier.weight(1f)) {
                                Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("SHOES", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = FitbunkViolet)
                                    Text(userProfile.sizeShoes.ifBlank { "UK 9" }, fontSize = 16.sp, fontWeight = FontWeight.Black, color = FitbunkDeepPurple)
                                }
                            }
                        }
                    }
                }
            }

            // Saved Delivery Addresses Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = FitbunkWhite),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Saved Addresses", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = FitbunkDeepPurple)

                        userProfile.addresses.forEach { addr ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = FitbunkLightBg,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Home, contentDescription = null, tint = FitbunkViolet, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(addr.label, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = FitbunkDeepPurple)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(addr.fullAddressText, fontSize = 12.sp, color = FitbunkTextSecondary)
                                }
                            }
                        }
                    }
                }
            }

            // Order History Section
            item {
                Text("Recent Orders", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = FitbunkDeepPurple)
            }

            if (orders.isEmpty()) {
                item {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = FitbunkWhite,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "No orders placed yet. Check out your bag or swipe to discover fits!",
                            color = FitbunkTextSecondary,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            } else {
                items(orders, key = { it.id }) { order ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { onOpenOrderTracking(order) }
                            .testTag("order_history_item_${order.id}"),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = FitbunkWhite),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Order #${order.id}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = FitbunkDeepPurple)
                                Text("${order.items.size} fits | Total: ₹${order.totalAmount}", fontSize = 12.sp, color = FitbunkTextSecondary)
                                Surface(shape = RoundedCornerShape(6.dp), color = FitbunkLavenderLight, modifier = Modifier.padding(top = 4.dp)) {
                                    Text(order.status.title, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = FitbunkDeepPurple, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                }
                            }

                            Button(
                                onClick = { onOpenOrderTracking(order) },
                                colors = ButtonDefaults.buttonColors(containerColor = FitbunkDeepPurple),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text("Track", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // Logout Option
            item {
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedButton(
                    onClick = onLogout,
                    modifier = Modifier.fillMaxWidth().testTag("profile_logout_btn"),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFCA5A5)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Logout, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("LOG OUT OF FITBUNK", fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(60.dp))
            }
        }

        // Edit Profile Dialog
        if (showEditProfileDialog) {
            AlertDialog(
                onDismissRequest = { showEditProfileDialog = false },
                title = { Text("Edit Profile Details", fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = editName,
                            onValueChange = { editName = it },
                            label = { Text("Name") },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp)
                        )
                        OutlinedTextField(
                            value = editEmail,
                            onValueChange = { editEmail = it },
                            label = { Text("Email") },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            onUpdateProfile(editName, editEmail)
                            showEditProfileDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = FitbunkViolet)
                    ) {
                        Text("Save")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showEditProfileDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        // Edit Sizes Dialog
        if (showEditSizesDialog) {
            var tempTops by remember { mutableStateOf(userProfile.sizeTops) }
            var tempBottoms by remember { mutableStateOf(userProfile.sizeBottoms) }
            var tempShoes by remember { mutableStateOf(userProfile.sizeShoes) }

            AlertDialog(
                onDismissRequest = { showEditSizesDialog = false },
                title = { Text("Update Fit Sizes", fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Tops (S, M, L, XL, XXL):", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("S", "M", "L", "XL", "XXL").forEach { s ->
                                FilterChip(
                                    selected = tempTops == s,
                                    onClick = { tempTops = s },
                                    label = { Text(s) }
                                )
                            }
                        }

                        Text("Bottoms (28, 30, 32, 34, 36):", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("28", "30", "32", "34", "36").forEach { s ->
                                FilterChip(
                                    selected = tempBottoms == s,
                                    onClick = { tempBottoms = s },
                                    label = { Text(s) }
                                )
                            }
                        }

                        Text("Shoes (UK 7, 8, 9, 10):", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("UK 7", "UK 8", "UK 9", "UK 10").forEach { s ->
                                FilterChip(
                                    selected = tempShoes == s,
                                    onClick = { tempShoes = s },
                                    label = { Text(s.replace("UK ", "")) }
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            onUpdateSizes(tempTops, tempBottoms, tempShoes)
                            showEditSizesDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = FitbunkViolet)
                    ) {
                        Text("Save")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showEditSizesDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

// ==========================================
// SCREEN 16: SETTINGS AND SUPPORT
// ==========================================
@Composable
fun SettingsAndSupportScreen(
    userProfile: UserProfile,
    onBackClick: () -> Unit,
    onUpdateNotifications: (push: Boolean, orders: Boolean, drops: Boolean) -> Unit,
    onDeleteAccount: () -> Unit
) {
    var pushNotifs by remember { mutableStateOf(userProfile.pushNotificationsEnabled) }
    var orderAlerts by remember { mutableStateOf(userProfile.orderAlertsEnabled) }
    var dropAlerts by remember { mutableStateOf(userProfile.dropAlertsEnabled) }

    var showDeleteAccountDialog by remember { mutableStateOf(false) }
    var showPrivacyPolicyDialog by remember { mutableStateOf(false) }
    var showTermsDialog by remember { mutableStateOf(false) }
    var showSupportContactDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            FitbunkTopBar(
                title = "Settings & Support",
                canNavigateBack = true,
                onBackClick = onBackClick
            )
        },
        containerColor = FitbunkLightBg
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .testTag("settings_screen_scroll"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Notification Preferences
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = FitbunkWhite),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Notifications", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = FitbunkDeepPurple)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Order Status Updates", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("Live courier dispatch and delivery alerts", fontSize = 11.sp, color = FitbunkTextSecondary)
                            }
                            Switch(
                                checked = orderAlerts,
                                onCheckedChange = {
                                    orderAlerts = it
                                    onUpdateNotifications(pushNotifs, orderAlerts, dropAlerts)
                                }
                            )
                        }

                        HorizontalDivider(color = FitbunkCardBorder)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Exclusive Drop Alerts", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("New streetwear drops and restock alerts", fontSize = 11.sp, color = FitbunkTextSecondary)
                            }
                            Switch(
                                checked = dropAlerts,
                                onCheckedChange = {
                                    dropAlerts = it
                                    onUpdateNotifications(pushNotifs, orderAlerts, dropAlerts)
                                }
                            )
                        }
                    }
                }
            }

            // FAQs Accordion
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = FitbunkWhite),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Frequently Asked Questions", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = FitbunkDeepPurple)

                        FaqItem(
                            question = "How does 60-minute delivery work?",
                            answer = "FITBUNK holds high-demand streetwear capsules in distributed city micro-fulfilment dark stores. Once bagged, local electric couriers dispatch directly to your doorstep."
                        )

                        FaqItem(
                            question = "What if a piece doesn't fit?",
                            answer = "We offer instant at-door exchange! Try on the size while our courier waits, or request a free 1-click pickup through the app."
                        )

                        FaqItem(
                            question = "Are the clothes authentic?",
                            answer = "100% genuine. FITBUNK partners directly with official streetwear design houses and verified independent designer labels."
                        )
                    }
                }
            }

            // Support & Legal Links
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = FitbunkWhite),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        SettingsRowItem(
                            title = "Contact Support & Concierge",
                            subtitle = "WhatsApp, Email & Instant Chat",
                            icon = Icons.Default.HeadsetMic,
                            onClick = { showSupportContactDialog = true }
                        )

                        HorizontalDivider(color = FitbunkCardBorder)

                        SettingsRowItem(
                            title = "Privacy Policy",
                            subtitle = "How we protect your data",
                            icon = Icons.Default.Security,
                            onClick = { showPrivacyPolicyDialog = true }
                        )

                        HorizontalDivider(color = FitbunkCardBorder)

                        SettingsRowItem(
                            title = "Terms & Conditions",
                            subtitle = "Hyperfast delivery terms",
                            icon = Icons.Default.Description,
                            onClick = { showTermsDialog = true }
                        )
                    }
                }
            }

            // Danger Zone: Delete Account
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = FitbunkWhite),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFCA5A5)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Account Actions", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFFDC2626))
                        Text("Deleting your account will permanently wipe your fit preferences, liked items, and saved addresses.", fontSize = 12.sp, color = FitbunkTextSecondary)

                        Button(
                            onClick = { showDeleteAccountDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("delete_account_btn")
                        ) {
                            Text("Delete Account", fontWeight = FontWeight.Bold, color = FitbunkWhite)
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(40.dp))
            }
        }

        // Support Dialog
        if (showSupportContactDialog) {
            AlertDialog(
                onDismissRequest = { showSupportContactDialog = false },
                title = { Text("FITBUNK Support Concierge", fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("⚡ Need assistance with a fit or active drop?", fontSize = 13.sp)
                        Text("• WhatsApp: +91 80 4920 1122", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("• Email: concierge@fitbunk.io", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("• Operating Hours: 7 AM – 2 AM Daily", fontSize = 12.sp, color = FitbunkTextSecondary)
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { showSupportContactDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = FitbunkViolet)
                    ) {
                        Text("Done")
                    }
                }
            )
        }

        // Privacy Policy Dialog
        if (showPrivacyPolicyDialog) {
            AlertDialog(
                onDismissRequest = { showPrivacyPolicyDialog = false },
                title = { Text("Privacy Policy", fontWeight = FontWeight.Bold) },
                text = {
                    Text(
                        "FITBUNK respects your privacy. We collect profile information and delivery coordinates solely to compute courier routing from our nearest micro-store. Data is encrypted and never sold to third parties."
                    )
                },
                confirmButton = {
                    Button(onClick = { showPrivacyPolicyDialog = false }) { Text("Close") }
                }
            )
        }

        // Terms Dialog
        if (showTermsDialog) {
            AlertDialog(
                onDismissRequest = { showTermsDialog = false },
                title = { Text("Terms of Service", fontWeight = FontWeight.Bold) },
                text = {
                    Text(
                        "Orders are delivered under the FITBUNK 60-Minute Guarantee subject to micro-hub serviceability. Exchanges are accepted within 7 days in unworn condition with brand tags attached."
                    )
                },
                confirmButton = {
                    Button(onClick = { showTermsDialog = false }) { Text("Close") }
                }
            )
        }

        // Delete Account Confirmation Dialog
        if (showDeleteAccountDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteAccountDialog = false },
                title = { Text("Delete FITBUNK Account?", color = Color(0xFFDC2626), fontWeight = FontWeight.Black) },
                text = {
                    Text("Are you sure? This cannot be undone. All your saved fits, discounts, and order histories will be erased permanently.")
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showDeleteAccountDialog = false
                            onDeleteAccount()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                    ) {
                        Text("Confirm Delete", color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteAccountDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
private fun FaqItem(question: String, answer: String) {
    var expanded by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable { expanded = !expanded }
            .padding(vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(question, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = FitbunkDeepPurple, modifier = Modifier.weight(1f))
            Icon(
                imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = null,
                tint = FitbunkViolet
            )
        }
        if (expanded) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(answer, fontSize = 12.sp, color = FitbunkTextSecondary, lineHeight = 17.sp)
        }
    }
}

@Composable
private fun SettingsRowItem(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = FitbunkViolet, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = FitbunkDeepPurple)
                Text(subtitle, fontSize = 11.sp, color = FitbunkTextSecondary)
            }
        }
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = FitbunkTextSecondary)
    }
}

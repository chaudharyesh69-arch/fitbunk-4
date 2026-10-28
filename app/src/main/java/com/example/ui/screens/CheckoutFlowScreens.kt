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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.components.FitbunkImage
import com.example.ui.components.FitbunkTopBar
import com.example.ui.theme.*

// ==========================================
// SCREEN 12: CHECKOUT
// ==========================================
@Composable
fun CheckoutScreen(
    cartItems: List<CartItem>,
    addresses: List<DeliveryAddress>,
    selectedAddressId: String,
    appliedCoupon: String?,
    onBackClick: () -> Unit,
    onSelectAddress: (String) -> Unit,
    onAddNewAddress: (DeliveryAddress) -> Unit,
    onPlaceOrder: (paymentMethod: String, address: DeliveryAddress) -> Unit
) {
    var selectedPaymentMethod by remember { mutableStateOf("UPI / Google Pay") }
    var upiIdInput by remember { mutableStateOf("alex@okhdfcbank") }
    var isPlacingOrder by remember { mutableStateOf(false) }

    val subtotal = cartItems.sumOf { it.product.price * it.quantity }
    val couponDiscount = when (appliedCoupon) {
        "FITBUNK100" -> if (subtotal >= 999) 100 else 0
        "DRIP50" -> if (subtotal >= 499) 50 else 0
        else -> 0
    }
    val deliveryFee = if (subtotal >= 999) 0 else 79
    val totalAmount = (subtotal - couponDiscount + deliveryFee).coerceAtLeast(0)

    val currentAddress = addresses.find { it.id == selectedAddressId } ?: addresses.firstOrNull() ?: DeliveryAddress(
        id = "default_addr",
        label = "Home",
        houseFlat = "Flat 402, Skyline Residency",
        street = "12th Main, 4th Block",
        area = "Koramangala",
        city = "Bengaluru",
        state = "Karnataka",
        pinCode = "560034"
    )

    val paymentMethods = listOf(
        "UPI / Google Pay",
        "PhonePe / Paytm",
        "Credit / Debit Card",
        "Cash on Delivery (COD)"
    )

    Scaffold(
        topBar = {
            FitbunkTopBar(
                title = "Checkout",
                canNavigateBack = true,
                onBackClick = onBackClick
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(16.dp),
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
                        Text("TOTAL AMOUNT", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = FitbunkTextSecondary)
                        Text("₹$totalAmount", fontSize = 22.sp, fontWeight = FontWeight.Black, color = FitbunkDeepPurple)
                    }

                    Button(
                        onClick = {
                            isPlacingOrder = true
                            onPlaceOrder(selectedPaymentMethod, currentAddress)
                        },
                        enabled = !isPlacingOrder && cartItems.isNotEmpty(),
                        modifier = Modifier
                            .height(52.dp)
                            .testTag("checkout_place_order_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = FitbunkDeepPurple),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        if (isPlacingOrder) {
                            CircularProgressIndicator(color = FitbunkWhite, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("CONFIRMING...")
                        } else {
                            Text("PLACE ORDER ⚡", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = FitbunkWhite)
                        }
                    }
                }
            }
        },
        containerColor = FitbunkLightBg
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Step 1: Delivery Address Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("checkout_address_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = FitbunkWhite),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.LocationOn, contentDescription = null, tint = FitbunkViolet)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("1. Delivery Address", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = FitbunkDeepPurple)
                            }
                            Surface(shape = RoundedCornerShape(6.dp), color = FitbunkLavenderLight) {
                                Text(currentAddress.label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = FitbunkDeepPurple, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
                            }
                        }

                        Text(
                            text = currentAddress.fullAddressText,
                            fontSize = 13.sp,
                            color = FitbunkTextPrimary,
                            lineHeight = 18.sp
                        )

                        Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFFD1FAE5)) {
                            Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.ElectricBolt, contentDescription = null, tint = FitbunkAccentGreen, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Hyperfast 60-min delivery active", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF065F46))
                            }
                        }
                    }
                }
            }

            // Step 2: Confirm Fits in Order
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = FitbunkWhite),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("2. Fit Summary (${cartItems.size} items)", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = FitbunkDeepPurple)

                        cartItems.forEach { item ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(item.product.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = FitbunkTextPrimary)
                                    Text("Size: ${item.selectedSize} | Color: ${item.selectedColor.name} | Qty: ${item.quantity}", fontSize = 11.sp, color = FitbunkTextSecondary)
                                }
                                Text("₹${item.product.price * item.quantity}", fontSize = 14.sp, fontWeight = FontWeight.Black, color = FitbunkDeepPurple)
                            }
                            HorizontalDivider(color = FitbunkCardBorder)
                        }
                    }
                }
            }

            // Step 3: Payment Method
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = FitbunkWhite),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("3. Payment Method", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = FitbunkDeepPurple)

                        paymentMethods.forEach { method ->
                            val isSelected = selectedPaymentMethod == method
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { selectedPaymentMethod = method }
                                    .testTag("payment_${method.take(5)}"),
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) FitbunkLavenderLight else FitbunkWhite,
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) FitbunkViolet else FitbunkCardBorder)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        RadioButton(
                                            selected = isSelected,
                                            onClick = { selectedPaymentMethod = method },
                                            colors = RadioButtonDefaults.colors(selectedColor = FitbunkViolet)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(method, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = FitbunkDeepPurple)
                                    }

                                    if (method.contains("UPI")) {
                                        Surface(shape = RoundedCornerShape(4.dp), color = FitbunkDeepPurple) {
                                            Text("FASTEST", color = FitbunkLavender, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                        }
                                    }
                                }
                            }
                        }

                        if (selectedPaymentMethod.contains("UPI")) {
                            OutlinedTextField(
                                value = upiIdInput,
                                onValueChange = { upiIdInput = it },
                                label = { Text("Virtual Payment Address (UPI ID)") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth().testTag("checkout_upi_input"),
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = FitbunkWhite, unfocusedContainerColor = FitbunkWhite)
                            )
                        }
                    }
                }
            }

            // Price Details
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = FitbunkWhite),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("4. Order Total Breakdown", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = FitbunkDeepPurple)

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Subtotal", fontSize = 13.sp, color = FitbunkTextSecondary)
                            Text("₹$subtotal", fontSize = 13.sp, color = FitbunkTextPrimary)
                        }

                        if (couponDiscount > 0) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Discount ($appliedCoupon)", fontSize = 13.sp, color = FitbunkTextSecondary)
                                Text("-₹$couponDiscount", fontSize = 13.sp, color = FitbunkAccentGreen, fontWeight = FontWeight.Bold)
                            }
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Delivery Fee", fontSize = 13.sp, color = FitbunkTextSecondary)
                            Text(if (deliveryFee == 0) "FREE" else "₹$deliveryFee", fontSize = 13.sp, color = if (deliveryFee == 0) FitbunkAccentGreen else FitbunkTextPrimary)
                        }

                        HorizontalDivider(color = FitbunkCardBorder)

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Final Payable", fontSize = 16.sp, fontWeight = FontWeight.Black, color = FitbunkDeepPurple)
                            Text("₹$totalAmount", fontSize = 18.sp, fontWeight = FontWeight.Black, color = FitbunkDeepPurple)
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(50.dp))
            }
        }
    }
}

// ==========================================
// SCREEN 13: ORDER SUCCESS
// ==========================================
@Composable
fun OrderSuccessScreen(
    order: Order,
    onTrackOrderClick: () -> Unit,
    onContinueShoppingClick: () -> Unit
) {
    Scaffold(containerColor = FitbunkLightBg) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
                .testTag("order_success_root"),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Success celebration circle
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(FitbunkViolet),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Success",
                        tint = FitbunkWhite,
                        modifier = Modifier.size(54.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "FIT BAGGED & SECURED!",
                    fontWeight = FontWeight.Black,
                    fontSize = 24.sp,
                    color = FitbunkDeepPurple,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Order Reference: ${order.id}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = FitbunkViolet
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Summary card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = FitbunkWhite),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Estimated Delivery", color = FitbunkTextSecondary, fontSize = 13.sp)
                            Text("⚡ In ${order.estimatedArrivalMins} Minutes", fontWeight = FontWeight.Bold, color = FitbunkDeepPurple, fontSize = 13.sp)
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Paid via", color = FitbunkTextSecondary, fontSize = 13.sp)
                            Text(order.paymentMethod, fontWeight = FontWeight.Bold, color = FitbunkDeepPurple, fontSize = 13.sp)
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total Amount", color = FitbunkTextSecondary, fontSize = 13.sp)
                            Text("₹${order.totalAmount}", fontWeight = FontWeight.Black, color = FitbunkViolet, fontSize = 15.sp)
                        }

                        HorizontalDivider(color = FitbunkCardBorder)

                        Text("Delivering to: ${order.deliveryAddress.fullAddressText}", fontSize = 12.sp, color = FitbunkTextSecondary, lineHeight = 16.sp)
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Primary CTA: TRACK ORDER
                Button(
                    onClick = onTrackOrderClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("success_track_order_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = FitbunkDeepPurple),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Default.Navigation, contentDescription = null, tint = FitbunkWhite)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("TRACK ORDER LIVE", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = FitbunkWhite)
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Secondary CTA: CONTINUE SHOPPING
                OutlinedButton(
                    onClick = onContinueShoppingClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("success_continue_shopping_btn"),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, FitbunkViolet)
                ) {
                    Text("CONTINUE SHOPPING", color = FitbunkViolet, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }
}

// ==========================================
// SCREEN 14: ORDER TRACKING
// ==========================================
@Composable
fun OrderTrackingScreen(
    order: Order,
    onBackClick: () -> Unit,
    onSimulateNextStatus: () -> Unit
) {
    val steps = listOf(
        OrderStatus.PLACED,
        OrderStatus.CONFIRMED,
        OrderStatus.PACKED,
        OrderStatus.OUT_FOR_DELIVERY,
        OrderStatus.DELIVERED
    )

    Scaffold(
        topBar = {
            FitbunkTopBar(
                title = "Track Order #${order.id}",
                canNavigateBack = true,
                onBackClick = onBackClick
            )
        },
        containerColor = FitbunkLightBg
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Live Status Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("tracking_status_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = FitbunkDeepPurple),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(shape = RoundedCornerShape(10.dp), color = FitbunkViolet) {
                                Text("⚡ DEMO LIVE TRACKING", color = FitbunkWhite, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                            }
                            Text("ETA: 35 Mins", color = FitbunkLavender, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = order.status.title,
                            fontWeight = FontWeight.Black,
                            fontSize = 24.sp,
                            color = FitbunkWhite
                        )

                        Text(
                            text = order.status.description,
                            fontSize = 14.sp,
                            color = FitbunkLavenderLight,
                            modifier = Modifier.padding(top = 4.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Button to advance status for live demo preview
                        Button(
                            onClick = onSimulateNextStatus,
                            colors = ButtonDefaults.buttonColors(containerColor = FitbunkLavender),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth().testTag("advance_order_demo_btn")
                        ) {
                            Text("Simulate Next Delivery Step ⏩", color = FitbunkDeepPurple, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }

            // Timeline Steps
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = FitbunkWhite),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text("Delivery Progress", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = FitbunkDeepPurple)

                        steps.forEachIndexed { index, step ->
                            val isCompleted = step.stepNumber <= order.status.stepNumber
                            val isCurrent = step == order.status

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (isCompleted) FitbunkViolet else FitbunkLightBg
                                        )
                                        .border(
                                            2.dp,
                                            if (isCompleted) FitbunkLavender else FitbunkCardBorder,
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isCompleted) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = FitbunkWhite, modifier = Modifier.size(16.dp))
                                    } else {
                                        Text("${index + 1}", fontSize = 12.sp, color = FitbunkTextSecondary, fontWeight = FontWeight.Bold)
                                    }
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Text(
                                        text = step.title,
                                        fontWeight = if (isCurrent) FontWeight.Black else FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = if (isCompleted) FitbunkDeepPurple else FitbunkTextSecondary
                                    )
                                    Text(
                                        text = step.description,
                                        fontSize = 11.sp,
                                        color = FitbunkTextSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Rider Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = FitbunkWhite),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(FitbunkLavenderLight),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.DeliveryDining, contentDescription = null, tint = FitbunkDeepPurple)
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(order.riderName, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = FitbunkDeepPurple)
                                Text("FITBUNK Rapid Courier", fontSize = 12.sp, color = FitbunkTextSecondary)
                            }
                        }

                        IconButton(
                            onClick = { /* Demo phone call trigger */ },
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(FitbunkViolet)
                        ) {
                            Icon(Icons.Default.Call, contentDescription = "Call Rider", tint = FitbunkWhite, modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

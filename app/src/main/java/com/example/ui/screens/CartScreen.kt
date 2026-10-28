package com.example.ui.screens

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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CartItem
import com.example.ui.components.FitbunkImage
import com.example.ui.components.FitbunkTopBar
import com.example.ui.theme.*

@Composable
fun CartScreen(
    cartItems: List<CartItem>,
    appliedCoupon: String?,
    onQuantityChange: (String, Int) -> Unit,
    onRemoveItem: (String) -> Unit,
    onApplyCoupon: (String) -> Boolean,
    onRemoveCoupon: () -> Unit,
    onProceedToCheckout: () -> Unit,
    onDiscoverFitsClick: () -> Unit
) {
    var promoCodeInput by remember { mutableStateOf("") }
    var promoError by remember { mutableStateOf<String?>(null) }

    val subtotal = cartItems.sumOf { it.product.price * it.quantity }
    val mrpTotal = cartItems.sumOf { it.product.originalPrice * it.quantity }
    val productDiscount = mrpTotal - subtotal
    val couponDiscount = when (appliedCoupon) {
        "FITBUNK100" -> if (subtotal >= 999) 100 else 0
        "DRIP50" -> if (subtotal >= 499) 50 else 0
        else -> 0
    }
    val deliveryFee = if (subtotal >= 999) 0 else if (cartItems.isNotEmpty()) 79 else 0
    val totalAmount = (subtotal - couponDiscount + deliveryFee).coerceAtLeast(0)

    Scaffold(
        topBar = {
            FitbunkTopBar(title = "My Bag (${cartItems.sumOf { it.quantity }})")
        },
        bottomBar = {
            if (cartItems.isNotEmpty()) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(16.dp),
                    color = FitbunkWhite,
                    tonalElevation = 8.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "TOTAL PAYABLE",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = FitbunkTextSecondary
                                )
                                Text(
                                    text = "₹$totalAmount",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Black,
                                    color = FitbunkDeepPurple
                                )
                            }

                            Button(
                                onClick = onProceedToCheckout,
                                modifier = Modifier
                                    .height(52.dp)
                                    .testTag("cart_proceed_checkout_btn"),
                                colors = ButtonDefaults.buttonColors(containerColor = FitbunkViolet),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Text("PROCEED TO CHECKOUT", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = FitbunkWhite)
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(Icons.Default.ArrowForward, contentDescription = null, tint = FitbunkWhite)
                            }
                        }
                    }
                }
            }
        },
        containerColor = FitbunkLightBg
    ) { padding ->
        if (cartItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp)
                    .testTag("cart_empty_state"),
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
                            imageVector = Icons.Outlined.ShoppingBag,
                            contentDescription = null,
                            tint = FitbunkViolet,
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Your bag is empty",
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp,
                        color = FitbunkDeepPurple
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Swipe right on discovery cards to instantly bag fits delivered within 60 minutes.",
                        fontSize = 14.sp,
                        color = FitbunkTextSecondary,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = onDiscoverFitsClick,
                        colors = ButtonDefaults.buttonColors(containerColor = FitbunkViolet),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.testTag("cart_discover_fits_btn")
                    ) {
                        Icon(Icons.Filled.LocalFireDepartment, contentDescription = null, tint = FitbunkWhite)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("DISCOVER FITS", fontWeight = FontWeight.Bold, color = FitbunkWhite)
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .testTag("cart_items_list"),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Free delivery progress banner
                item {
                    val threshold = 999
                    val progress = (subtotal.toFloat() / threshold).coerceIn(0f, 1f)
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = FitbunkLavenderLight,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = if (subtotal >= threshold) "🎉 Free Instant Delivery Unlocked!" else "Add ₹${threshold - subtotal} more for Free Delivery",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = FitbunkDeepPurple
                                )
                                Text(
                                    text = if (subtotal >= threshold) "FREE" else "₹79",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black,
                                    color = FitbunkDeepPurple
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            LinearProgressIndicator(
                                progress = { progress },
                                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                                color = FitbunkViolet,
                                trackColor = FitbunkWhite
                            )
                        }
                    }
                }

                // Cart items
                items(
                    items = cartItems,
                    key = { it.id },
                    contentType = { "cart_item" }
                ) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .testTag("cart_item_${item.id}"),
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
                                    .size(86.dp)
                                    .clip(RoundedCornerShape(12.dp))
                            ) {
                                FitbunkImage(
                                    imageUrl = item.product.images.firstOrNull() ?: "",
                                    contentDescription = item.product.name,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.product.brand,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = FitbunkViolet
                                )

                                Text(
                                    text = item.product.name,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = FitbunkTextPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = FitbunkLavenderLight
                                    ) {
                                        Text(
                                            text = "Size: ${item.selectedSize}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = FitbunkDeepPurple,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = Color(item.selectedColor.hex).copy(alpha = 0.2f)
                                    ) {
                                        Text(
                                            text = item.selectedColor.name,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = FitbunkDeepPurple,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "₹${item.product.price * item.quantity}",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Black,
                                        color = FitbunkDeepPurple
                                    )

                                    // Quantity Stepper
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .border(1.dp, FitbunkCardBorder, RoundedCornerShape(8.dp))
                                            .background(FitbunkLightBg, RoundedCornerShape(8.dp))
                                    ) {
                                        IconButton(
                                            onClick = { onQuantityChange(item.id, -1) },
                                            modifier = Modifier.size(30.dp).testTag("qty_minus_${item.id}")
                                        ) {
                                            Icon(Icons.Default.Remove, contentDescription = "Decrease", modifier = Modifier.size(16.dp))
                                        }

                                        Text(
                                            text = "${item.quantity}",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 8.dp)
                                        )

                                        IconButton(
                                            onClick = { onQuantityChange(item.id, 1) },
                                            modifier = Modifier.size(30.dp).testTag("qty_plus_${item.id}")
                                        ) {
                                            Icon(Icons.Default.Add, contentDescription = "Increase", modifier = Modifier.size(16.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Promo Coupon Section
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = FitbunkWhite),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Coupons & Offers", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = FitbunkDeepPurple)

                            Spacer(modifier = Modifier.height(10.dp))

                            if (appliedCoupon != null) {
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = Color(0xFFD1FAE5),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = FitbunkAccentGreen, modifier = Modifier.size(18.dp))
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("Coupon '$appliedCoupon' Applied! Saved ₹$couponDiscount", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF065F46))
                                        }

                                        TextButton(onClick = onRemoveCoupon, modifier = Modifier.testTag("remove_coupon_btn")) {
                                            Text("Remove", color = Color(0xFFDC2626), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            } else {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedTextField(
                                        value = promoCodeInput,
                                        onValueChange = { promoCodeInput = it; promoError = null },
                                        placeholder = { Text("Enter FITBUNK100", fontSize = 12.sp) },
                                        singleLine = true,
                                        modifier = Modifier.weight(1f).testTag("coupon_input"),
                                        shape = RoundedCornerShape(10.dp),
                                        colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = FitbunkWhite, unfocusedContainerColor = FitbunkWhite)
                                    )

                                    Button(
                                        onClick = {
                                            if (promoCodeInput.isNotBlank()) {
                                                val success = onApplyCoupon(promoCodeInput)
                                                if (!success) {
                                                    promoError = "Invalid coupon code. Try 'FITBUNK100'"
                                                }
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = FitbunkDeepPurple),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.testTag("apply_coupon_btn")
                                    ) {
                                        Text("APPLY", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
                                }

                                if (promoError != null) {
                                    Text(promoError ?: "", color = FitbunkAccentPink, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
                                }
                            }
                        }
                    }
                }

                // Price Breakdown Bill Details
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = FitbunkWhite),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text("Bill Details", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = FitbunkDeepPurple)

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Total MRP", fontSize = 13.sp, color = FitbunkTextSecondary)
                                Text("₹$mrpTotal", fontSize = 13.sp, color = FitbunkTextPrimary)
                            }

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Product Discount", fontSize = 13.sp, color = FitbunkTextSecondary)
                                Text("-₹$productDiscount", fontSize = 13.sp, color = FitbunkAccentGreen, fontWeight = FontWeight.Bold)
                            }

                            if (couponDiscount > 0) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Coupon Discount ($appliedCoupon)", fontSize = 13.sp, color = FitbunkTextSecondary)
                                    Text("-₹$couponDiscount", fontSize = 13.sp, color = FitbunkAccentGreen, fontWeight = FontWeight.Bold)
                                }
                            }

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Delivery Fee (60m Instant)", fontSize = 13.sp, color = FitbunkTextSecondary)
                                Text(if (deliveryFee == 0) "FREE" else "₹$deliveryFee", fontSize = 13.sp, color = if (deliveryFee == 0) FitbunkAccentGreen else FitbunkTextPrimary, fontWeight = FontWeight.Bold)
                            }

                            HorizontalDivider(color = FitbunkCardBorder)

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Grand Total", fontSize = 16.sp, fontWeight = FontWeight.Black, color = FitbunkDeepPurple)
                                Text("₹$totalAmount", fontSize = 18.sp, fontWeight = FontWeight.Black, color = FitbunkDeepPurple)
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(60.dp))
                }
            }
        }
    }
}

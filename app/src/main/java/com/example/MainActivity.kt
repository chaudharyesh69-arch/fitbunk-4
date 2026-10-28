package com.example

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.navigation.Screen
import com.example.ui.FitbunkViewModel
import com.example.ui.components.FitbunkBottomNav
import com.example.ui.screens.*
import com.example.ui.theme.FitbunkTheme
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.appcheck.recaptcha.RecaptchaAppCheckProviderFactory
import kotlinx.coroutines.flow.collectLatest

private const val TAG_APPCHECK = "AppCheckInit"
private const val DEBUG_TOKEN_EXTRA = "FIREBASE_APPCHECK_DEBUG_TOKEN"
private const val DEBUG_STORE_PREFIX = "com.google.firebase.appcheck.debug.store."
private const val DEBUG_SECRET_KEY = "com.google.firebase.appcheck.debug.DEBUG_SECRET"
private const val RECAPTCHA_SITE_KEY_RES = "recaptcha_site_key"

fun configureAppCheck(context: Context, intent: Intent?) {
    try {
        if (FirebaseApp.getApps(context).isEmpty()) {
            FirebaseApp.initializeApp(context)
        }

        if (BuildConfig.DEBUG) {
            val debugToken = intent?.getStringExtra(DEBUG_TOKEN_EXTRA)
            if (!debugToken.isNullOrEmpty()) {
                val persistenceKey = FirebaseApp.getInstance().persistenceKey
                val prefs = context.getSharedPreferences(
                    "$DEBUG_STORE_PREFIX$persistenceKey",
                    Context.MODE_PRIVATE
                )
                // Use commit() synchronously so the debug secret is written before factory reads it
                prefs.edit()
                    .putString(DEBUG_SECRET_KEY, debugToken)
                    .commit()
                Log.d(TAG_APPCHECK, "App Check debug secret configured successfully.")
            } else {
                Log.w(TAG_APPCHECK, "No debug token found in Intent extra.")
            }

            FirebaseAppCheck.getInstance().installAppCheckProviderFactory(
                DebugAppCheckProviderFactory.getInstance()
            )
            Log.d(TAG_APPCHECK, "DebugAppCheckProviderFactory installed.")
        } else {
            val resId = context.resources.getIdentifier(RECAPTCHA_SITE_KEY_RES, "string", context.packageName)
            if (resId != 0) {
                val siteKey = context.getString(resId)
                if (siteKey.isNotBlank()) {
                    FirebaseAppCheck.getInstance().installAppCheckProviderFactory(
                        RecaptchaAppCheckProviderFactory.getInstance(siteKey)
                    )
                    Log.d(TAG_APPCHECK, "RecaptchaAppCheckProviderFactory installed.")
                }
            }
        }
    } catch (e: Exception) {
        Log.e(TAG_APPCHECK, "Failed to initialize Firebase App Check", e)
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Step 1: Configure App Check before setContent or ViewModels
        configureAppCheck(this, intent)
        enableEdgeToEdge()
        setContent {
            FitbunkTheme {
                FitbunkApp()
            }
        }
    }
}

@Composable
fun FitbunkApp(viewModel: FitbunkViewModel = viewModel()) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()

    val products by viewModel.repository.products.collectAsStateWithLifecycle()
    val activeDeckProducts by viewModel.activeDeckProducts.collectAsStateWithLifecycle()
    val selectedBrand by viewModel.selectedBrand.collectAsStateWithLifecycle()
    val likedProducts by viewModel.repository.likedProducts.collectAsStateWithLifecycle()
    val cartItems by viewModel.repository.cartItems.collectAsStateWithLifecycle()
    val userProfile by viewModel.repository.userProfile.collectAsStateWithLifecycle()
    val orders by viewModel.repository.orders.collectAsStateWithLifecycle()
    val currentDeckIndex by viewModel.repository.currentDeckIndex.collectAsStateWithLifecycle()
    val swipeHistory by viewModel.repository.swipeHistory.collectAsStateWithLifecycle()
    val appliedCoupon by viewModel.repository.appliedCoupon.collectAsStateWithLifecycle()

    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val selectedStyle by viewModel.selectedStyle.collectAsStateWithLifecycle()
    val selectedSort by viewModel.selectedSort.collectAsStateWithLifecycle()
    val searchResults by viewModel.searchResults.collectAsStateWithLifecycle()

    val selectedProduct by viewModel.selectedProduct.collectAsStateWithLifecycle()
    val activeOrder by viewModel.activeOrder.collectAsStateWithLifecycle()
    val pendingSizePicker by viewModel.pendingSizePick.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.snackbarMessage.collectLatest { message ->
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
        }
    }

    // BackHandler implementation across all screens
    BackHandler(enabled = currentScreen !is Screen.Discover && currentScreen !is Screen.Splash) {
        when (currentScreen) {
            is Screen.Search, is Screen.Liked, is Screen.Cart, is Screen.Profile -> {
                viewModel.switchTab(Screen.Discover)
            }
            else -> {
                val navigated = viewModel.navigateBack()
                if (!navigated) {
                    viewModel.switchTab(Screen.Discover)
                }
            }
        }
    }

    // Determine bottom navigation visibility
    val isBottomNavVisible = when (currentScreen) {
        is Screen.Discover, is Screen.Search, is Screen.Liked, is Screen.Cart, is Screen.Profile -> true
        else -> false
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .navigationBarsPadding()
                    .padding(bottom = if (isBottomNavVisible) 70.dp else 16.dp)
            )
        },
        bottomBar = {
            AnimatedVisibility(
                visible = isBottomNavVisible,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                FitbunkBottomNav(
                    currentTab = currentTab,
                    cartItemCount = cartItems.sumOf { it.quantity },
                    likedItemCount = likedProducts.size,
                    onTabSelected = { tab -> viewModel.switchTab(tab) }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = if (isBottomNavVisible) innerPadding.calculateBottomPadding() else 0.dp)
        ) {
            when (val screen = currentScreen) {
                // SCREEN 1: SPLASH
                is Screen.Splash -> {
                    SplashScreen(
                        onTimeout = {
                            viewModel.replaceTop(Screen.Welcome)
                        }
                    )
                }

                // SCREEN 2: WELCOME
                is Screen.Welcome -> {
                    WelcomeScreen(
                        onLetsGoClick = {
                            viewModel.navigateTo(Screen.Auth)
                        },
                        onAlreadyAccountClick = {
                            viewModel.navigateTo(Screen.Auth)
                        }
                    )
                }

                // SCREEN 3: AUTH (Sign Up / Login / Guest)
                is Screen.Auth -> {
                    AuthScreen(
                        onAuthSuccess = { name, email ->
                            viewModel.repository.updateUserProfile(
                                name = name,
                                email = email,
                                ageConfirmed = true,
                                categories = userProfile.preferredCategories,
                                style = userProfile.preferredStyle,
                                budget = userProfile.budgetPreference
                            )
                            viewModel.navigateTo(Screen.ProfileSetup)
                        },
                        onGuestClick = {
                            viewModel.navigateTo(Screen.Discover)
                        }
                    )
                }

                // SCREEN 4: PROFILE SETUP
                is Screen.ProfileSetup -> {
                    ProfileSetupScreen(
                        currentProfile = userProfile,
                        onSaveAndContinue = { name, ageConfirmed, categories, style, budget ->
                            viewModel.repository.updateUserProfile(
                                name = name,
                                email = userProfile.email,
                                ageConfirmed = ageConfirmed,
                                categories = categories,
                                style = style,
                                budget = budget
                            )
                            viewModel.navigateTo(Screen.SizePreferences)
                        },
                        onSkip = {
                            viewModel.navigateTo(Screen.SizePreferences)
                        }
                    )
                }

                // SCREEN 5: SIZE PREFERENCES
                is Screen.SizePreferences -> {
                    SizePreferencesScreen(
                        currentProfile = userProfile,
                        onSaveAndContinue = { tops, bottoms, shoes ->
                            viewModel.repository.updateSizes(tops, bottoms, shoes)
                            viewModel.navigateTo(Screen.LocationSetup)
                        },
                        onSkip = {
                            viewModel.navigateTo(Screen.LocationSetup)
                        }
                    )
                }

                // SCREEN 6: LOCATION AND DELIVERY
                is Screen.LocationSetup -> {
                    LocationSetupScreen(
                        currentAddresses = userProfile.addresses,
                        onSaveAddressAndFinish = { address ->
                            viewModel.repository.addAddress(address)
                            viewModel.navigateTo(Screen.Discover)
                        },
                        onSkip = {
                            viewModel.navigateTo(Screen.Discover)
                        }
                    )
                }

                // SCREEN 7: MAIN DISCOVER (Tinder-style swipe)
                is Screen.Discover -> {
                    DiscoverScreen(
                        products = activeDeckProducts,
                        currentIndex = currentDeckIndex,
                        cartItemCount = cartItems.sumOf { it.quantity },
                        canUndo = swipeHistory.isNotEmpty() && currentDeckIndex > 0,
                        brands = viewModel.brands,
                        selectedBrand = selectedBrand,
                        onBrandSelect = { brand -> viewModel.onBrandSelect(brand) },
                        onSwipeRight = { product -> viewModel.onSwipeRight(product) },
                        onSwipeLeft = { product -> viewModel.onSwipeLeft(product) },
                        onUndoClick = { viewModel.onUndoSwipe() },
                        onResetDeckClick = { viewModel.onResetDeck() },
                        onProductClick = { product -> viewModel.openProductDetails(product) },
                        pendingSizePickerProduct = pendingSizePicker?.product,
                        onConfirmSizePick = { size -> viewModel.confirmPendingSizePick(size) },
                        onDismissSizePick = { viewModel.dismissPendingSizePick() },
                        onOpenVirtualTryOn = { product -> viewModel.openVirtualTryOn(product) }
                    )
                }

                // SCREEN 8: PRODUCT DETAILS
                is Screen.ProductDetail -> {
                    val product = selectedProduct ?: products.first()
                    ProductDetailScreen(
                        product = product,
                        isLiked = likedProducts.any { it.id == product.id },
                        onBackClick = { viewModel.navigateBack() },
                        onToggleLike = { viewModel.onToggleLike(product) },
                        onAddToCart = { prod, size, color ->
                            viewModel.onAddToCart(prod, size, color)
                        },
                        onOpenVirtualTryOn = { viewModel.openVirtualTryOn(product) }
                    )
                }

                // SCREEN 9: SEARCH AND CATEGORIES
                is Screen.Search -> {
                    SearchScreen(
                        products = searchResults,
                        likedProductIds = likedProducts.map { it.id }.toSet(),
                        brands = viewModel.brands,
                        selectedBrand = selectedBrand,
                        searchQuery = searchQuery,
                        selectedCategory = selectedCategory,
                        selectedStyle = selectedStyle,
                        selectedSort = selectedSort,
                        onBrandChange = { brand -> viewModel.onBrandSelect(brand) },
                        onQueryChange = { viewModel.onSearchQueryChange(it) },
                        onCategoryChange = { viewModel.onCategorySelect(it) },
                        onStyleChange = { viewModel.onStyleSelect(it) },
                        onSortChange = { viewModel.onSortSelect(it) },
                        onProductClick = { product -> viewModel.openProductDetails(product) },
                        onToggleLike = { product -> viewModel.onToggleLike(product) }
                    )
                }

                // SCREEN 10: LIKED OUTFITS
                is Screen.Liked -> {
                    LikedScreen(
                        likedProducts = likedProducts,
                        userDefaultSize = userProfile.sizeTops,
                        onProductClick = { product -> viewModel.openProductDetails(product) },
                        onRemoveLiked = { product -> viewModel.onToggleLike(product) },
                        onAddToCart = { prod, size, color -> viewModel.onAddToCart(prod, size, color) },
                        onStartSwipingClick = { viewModel.switchTab(Screen.Discover) }
                    )
                }

                // SCREEN 11: SHOPPING CART
                is Screen.Cart -> {
                    CartScreen(
                        cartItems = cartItems,
                        appliedCoupon = appliedCoupon,
                        onQuantityChange = { id, delta -> viewModel.repository.updateCartQuantity(id, delta) },
                        onRemoveItem = { id -> viewModel.repository.removeFromCart(id) },
                        onApplyCoupon = { code ->
                            val success = viewModel.repository.applyCoupon(code)
                            if (success) viewModel.emitSnackbar("Coupon applied successfully! 🎉")
                            success
                        },
                        onRemoveCoupon = {
                            viewModel.repository.removeCoupon()
                            viewModel.emitSnackbar("Coupon removed")
                        },
                        onProceedToCheckout = { viewModel.navigateTo(Screen.Checkout) },
                        onDiscoverFitsClick = { viewModel.switchTab(Screen.Discover) }
                    )
                }

                // SCREEN 12: CHECKOUT
                is Screen.Checkout -> {
                    CheckoutScreen(
                        cartItems = cartItems,
                        addresses = userProfile.addresses,
                        selectedAddressId = userProfile.selectedAddressId,
                        appliedCoupon = appliedCoupon,
                        onBackClick = { viewModel.navigateBack() },
                        onSelectAddress = { id -> viewModel.repository.selectAddress(id) },
                        onAddNewAddress = { addr -> viewModel.repository.addAddress(addr) },
                        onPlaceOrder = { paymentMethod, address ->
                            val newOrder = viewModel.repository.placeOrder(paymentMethod, address)
                            viewModel.openOrderSuccess(newOrder)
                        }
                    )
                }

                // AI VIRTUAL TRY-ON STUDIO
                is Screen.VirtualTryOn -> {
                    val initialProd = selectedProduct
                        ?: (if (screen.productId != null) products.firstOrNull { it.id == screen.productId } else null)
                        ?: products.first()
                    VirtualTryOnScreen(
                        initialProduct = initialProd,
                        allProducts = products,
                        userDefaultSize = userProfile.sizeTops,
                        onBackClick = { viewModel.navigateBack() },
                        onAddToCart = { prod, size, color ->
                            viewModel.onAddToCart(prod, size, color)
                        },
                        onNavigateToCart = { viewModel.switchTab(Screen.Cart) }
                    )
                }

                // SCREEN 13: ORDER SUCCESS
                is Screen.OrderSuccess -> {
                    val order = activeOrder ?: orders.firstOrNull() ?: viewModel.repository.placeOrder("UPI", userProfile.addresses.first())
                    OrderSuccessScreen(
                        order = order,
                        onTrackOrderClick = { viewModel.openOrderTracking(order) },
                        onContinueShoppingClick = { viewModel.switchTab(Screen.Discover) }
                    )
                }

                // SCREEN 14: ORDER TRACKING
                is Screen.OrderTracking -> {
                    val order = activeOrder ?: orders.firstOrNull() ?: viewModel.repository.placeOrder("UPI", userProfile.addresses.first())
                    OrderTrackingScreen(
                        order = order,
                        onBackClick = { viewModel.navigateBack() },
                        onSimulateNextStatus = {
                            viewModel.repository.advanceDemoOrderStatus(order.id)
                        }
                    )
                }

                // SCREEN 15: PROFILE
                is Screen.Profile -> {
                    ProfileScreen(
                        userProfile = userProfile,
                        likedCount = likedProducts.size,
                        orders = orders,
                        onOpenOrderTracking = { order -> viewModel.openOrderTracking(order) },
                        onOpenSettings = { viewModel.navigateTo(Screen.SettingsAndSupport) },
                        onOpenLiked = { viewModel.switchTab(Screen.Liked) },
                        onUpdateProfile = { name, email ->
                            viewModel.repository.updateUserProfile(
                                name = name,
                                email = email,
                                ageConfirmed = userProfile.ageConfirmed,
                                categories = userProfile.preferredCategories,
                                style = userProfile.preferredStyle,
                                budget = userProfile.budgetPreference
                            )
                            viewModel.emitSnackbar("Profile updated! ✨")
                        },
                        onUpdateSizes = { tops, bottoms, shoes ->
                            viewModel.repository.updateSizes(tops, bottoms, shoes)
                            viewModel.emitSnackbar("Fit sizes updated! 📏")
                        },
                        onLogout = {
                            viewModel.navigateTo(Screen.Auth)
                            viewModel.emitSnackbar("Logged out of FITBUNK")
                        }
                    )
                }

                // SCREEN 16: SETTINGS AND SUPPORT
                is Screen.SettingsAndSupport -> {
                    SettingsAndSupportScreen(
                        userProfile = userProfile,
                        onBackClick = { viewModel.navigateBack() },
                        onUpdateNotifications = { push, orderAl, drops ->
                            viewModel.repository.updateNotificationPreferences(push, orderAl, drops)
                            viewModel.emitSnackbar("Notification preferences saved")
                        },
                        onDeleteAccount = {
                            viewModel.repository.clearCart()
                            viewModel.navigateTo(Screen.Auth)
                            viewModel.emitSnackbar("Account data deleted")
                        }
                    )
                }
            }
        }
    }
}

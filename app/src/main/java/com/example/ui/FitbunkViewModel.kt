package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.FitbunkRepository
import com.example.data.SampleData
import com.example.model.*
import com.example.navigation.Screen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class FitbunkViewModel(
    val repository: FitbunkRepository = FitbunkRepository()
) : ViewModel() {

    val brands: List<BrandInfo> = SampleData.brands

    // Navigation Stack
    private val _backStack = MutableStateFlow<List<Screen>>(listOf(Screen.Splash))
    val backStack: StateFlow<List<Screen>> = _backStack.asStateFlow()

    val currentScreen: StateFlow<Screen> = _backStack.map { it.lastOrNull() ?: Screen.Splash }
        .stateIn(viewModelScope, SharingStarted.Eagerly, Screen.Splash)

    // Current Bottom Nav Tab
    private val _currentTab = MutableStateFlow<Screen>(Screen.Discover)
    val currentTab: StateFlow<Screen> = _currentTab.asStateFlow()

    // Brand filtering state
    private val _selectedBrand = MutableStateFlow<String?>(null)
    val selectedBrand: StateFlow<String?> = _selectedBrand.asStateFlow()

    // Deck products filtered by selected brand
    val activeDeckProducts: StateFlow<List<Product>> = combine(
        repository.products,
        _selectedBrand
    ) { products, brand ->
        if (brand.isNullOrBlank() || brand == "All Brands") {
            products
        } else {
            products.filter { it.brand.equals(brand, ignoreCase = true) }
        }
    }.flowOn(Dispatchers.Default)
    .stateIn(viewModelScope, SharingStarted.Eagerly, repository.products.value)

    // Transient UI states
    private val _snackbarMessage = MutableSharedFlow<String>()
    val snackbarMessage: SharedFlow<String> = _snackbarMessage.asSharedFlow()

    // Size Selection Popup state for Swipe Right when size not configured
    data class SizePickerRequest(
        val product: Product,
        val color: ProductColor
    )
    private val _pendingSizePick = MutableStateFlow<SizePickerRequest?>(null)
    val pendingSizePick: StateFlow<SizePickerRequest?> = _pendingSizePick.asStateFlow()

    // Selected product for detail screen
    private val _selectedProduct = MutableStateFlow<Product?>(repository.products.value.firstOrNull())
    val selectedProduct: StateFlow<Product?> = _selectedProduct.asStateFlow()

    // Selected order for success/tracking
    private val _activeOrder = MutableStateFlow<Order?>(null)
    val activeOrder: StateFlow<Order?> = _activeOrder.asStateFlow()

    // Search & Filter state
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow(ProductCategory.ALL)
    val selectedCategory: StateFlow<ProductCategory> = _selectedCategory.asStateFlow()

    private val _selectedStyle = MutableStateFlow<StyleTag?>(null)
    val selectedStyle: StateFlow<StyleTag?> = _selectedStyle.asStateFlow()

    private val _selectedSort = MutableStateFlow("Recommended")
    val selectedSort: StateFlow<String> = _selectedSort.asStateFlow()

    // Filtered search results across 1000 items
    val searchResults: StateFlow<List<Product>> = combine(
        repository.products,
        _searchQuery,
        _selectedCategory,
        _selectedBrand,
        _selectedSort
    ) { products, query, category, brand, sort ->
        var list = products.filter { product ->
            val matchesQuery = query.isBlank() ||
                product.name.contains(query, ignoreCase = true) ||
                product.brand.contains(query, ignoreCase = true) ||
                product.category.label.contains(query, ignoreCase = true)

            val matchesCategory = category == ProductCategory.ALL || product.category == category
            val matchesBrand = brand.isNullOrBlank() || brand == "All Brands" || product.brand.equals(brand, ignoreCase = true)

            matchesQuery && matchesCategory && matchesBrand
        }

        when (sort) {
            "Price: Low to High" -> list.sortedBy { it.price }
            "Price: High to Low" -> list.sortedByDescending { it.price }
            "Rating" -> list.sortedByDescending { it.rating }
            else -> list
        }
    }.flowOn(Dispatchers.Default)
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), repository.products.value)

    // Navigation Methods
    fun navigateTo(screen: Screen) {
        if (screen is Screen.Discover || screen is Screen.Search ||
            screen is Screen.Liked || screen is Screen.Cart || screen is Screen.Profile
        ) {
            _currentTab.value = screen
        }
        _backStack.update { it + screen }
    }

    fun replaceTop(screen: Screen) {
        if (screen is Screen.Discover || screen is Screen.Search ||
            screen is Screen.Liked || screen is Screen.Cart || screen is Screen.Profile
        ) {
            _currentTab.value = screen
        }
        _backStack.update { stack ->
            if (stack.isNotEmpty()) stack.dropLast(1) + screen else listOf(screen)
        }
    }

    fun switchTab(tab: Screen) {
        _currentTab.value = tab
        _backStack.update { stack ->
            if (stack.lastOrNull() == tab) return@update stack
            listOf(tab)
        }
    }

    fun navigateBack(): Boolean {
        val current = _backStack.value
        return if (current.size > 1) {
            _backStack.update { it.dropLast(1) }
            val newTop = _backStack.value.last()
            if (newTop is Screen.Discover || newTop is Screen.Search ||
                newTop is Screen.Liked || newTop is Screen.Cart || newTop is Screen.Profile
            ) {
                _currentTab.value = newTop
            }
            true
        } else {
            false
        }
    }

    fun openProductDetails(product: Product) {
        _selectedProduct.value = product
        navigateTo(Screen.ProductDetail(product.id))
    }

    fun openOrderSuccess(order: Order) {
        _activeOrder.value = order
        navigateTo(Screen.OrderSuccess(order.id))
    }

    fun openOrderTracking(order: Order) {
        _activeOrder.value = order
        navigateTo(Screen.OrderTracking(order.id))
    }

    // Brand Selection Handling
    fun onBrandSelect(brand: String?) {
        val finalBrand = if (brand == "All Brands") null else brand
        _selectedBrand.value = finalBrand
        repository.resetDeck()
        emitSnackbar(
            if (finalBrand != null) "Browsing $finalBrand exclusive inventory (100 fits) ⚡"
            else "Browsing All 1,000 Drops ✨"
        )
    }

    // Swipe Deck Handling
    fun onSwipeRight(product: Product) {
        val profile = repository.userProfile.value
        val defaultSize = profile.sizeTops.ifBlank { product.sizes.firstOrNull() ?: "M" }
        val defaultColor = product.colors.firstOrNull() ?: ProductColor("Standard", 0xFF000000)

        // If size is set in profile, auto add to cart!
        if (profile.sizeTops.isNotBlank()) {
            repository.swipeRight(product, defaultSize, defaultColor)
            emitSnackbar("Added ${product.name} (Size $defaultSize) to Cart! 🛒")
        } else {
            _pendingSizePick.value = SizePickerRequest(product, defaultColor)
        }
    }

    fun confirmPendingSizePick(size: String) {
        val req = _pendingSizePick.value ?: return
        repository.swipeRight(req.product, size, req.color)
        _pendingSizePick.value = null
        emitSnackbar("Added ${req.product.name} (Size $size) to Cart! 🛒")
    }

    fun dismissPendingSizePick() {
        _pendingSizePick.value = null
    }

    fun onSwipeLeft(product: Product) {
        repository.swipeLeft(product)
    }

    fun onUndoSwipe() {
        val undone = repository.undoLastSwipe()
        if (undone) {
            emitSnackbar("Undid last swipe ↺")
        }
    }

    fun onResetDeck() {
        repository.resetDeck()
        emitSnackbar("Refreshed discovery stream ✨")
    }

    fun onToggleLike(product: Product) {
        repository.toggleLike(product)
        val isLiked = repository.isProductLiked(product.id)
        emitSnackbar(if (isLiked) "Saved to your Liked fits ❤️" else "Removed from Liked fits")
    }

    fun onAddToCart(product: Product, size: String, color: ProductColor, qty: Int = 1) {
        repository.addToCart(product, size, color, qty)
        emitSnackbar("Added $qty x ${product.name} to Cart 🛒")
    }

    // Search filters
    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onCategorySelect(category: ProductCategory) {
        _selectedCategory.value = category
    }

    fun onStyleSelect(style: StyleTag?) {
        _selectedStyle.value = style
    }

    fun onSortSelect(sort: String) {
        _selectedSort.value = sort
    }

    fun emitSnackbar(message: String) {
        viewModelScope.launch {
            _snackbarMessage.emit(message)
        }
    }
}

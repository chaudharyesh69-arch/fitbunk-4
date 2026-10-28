package com.example.data

import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

sealed class SwipeAction {
    data class Liked(val product: Product, val addedToCart: Boolean, val cartItemId: String?) : SwipeAction()
    data class Passed(val product: Product) : SwipeAction()
}

class FitbunkRepository {
    private val _products = MutableStateFlow<List<Product>>(SampleData.sampleProducts)
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    private val _likedProducts = MutableStateFlow<List<Product>>(
        listOf(SampleData.sampleProducts[0], SampleData.sampleProducts[2])
    )
    val likedProducts: StateFlow<List<Product>> = _likedProducts.asStateFlow()

    private val _cartItems = MutableStateFlow<List<CartItem>>(
        listOf(
            CartItem(
                id = "cart_init_1",
                product = SampleData.sampleProducts[0],
                selectedSize = "L",
                selectedColor = SampleData.sampleProducts[0].colors[0],
                quantity = 1
            )
        )
    )
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    private val _currentDeckIndex = MutableStateFlow(0)
    val currentDeckIndex: StateFlow<Int> = _currentDeckIndex.asStateFlow()

    private val _swipeHistory = MutableStateFlow<List<SwipeAction>>(emptyList())
    val swipeHistory: StateFlow<List<SwipeAction>> = _swipeHistory.asStateFlow()

    private val _userProfile = MutableStateFlow(UserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()

    private val _appliedCoupon = MutableStateFlow<String?>("FITBUNK100")
    val appliedCoupon: StateFlow<String?> = _appliedCoupon.asStateFlow()

    fun swipeRight(product: Product, size: String, color: ProductColor): Boolean {
        // Add to liked
        _likedProducts.update { current ->
            if (current.none { it.id == product.id }) current + product else current
        }

        // Add to cart
        var addedCartId: String? = null
        if (size.isNotBlank()) {
            val existing = _cartItems.value.find {
                it.product.id == product.id && it.selectedSize == size && it.selectedColor.name == color.name
            }
            if (existing != null) {
                _cartItems.update { list ->
                    list.map { if (it.id == existing.id) it.copy(quantity = it.quantity + 1) else it }
                }
                addedCartId = existing.id
            } else {
                val newItem = CartItem(
                    id = "cart_${UUID.randomUUID().toString().take(8)}",
                    product = product,
                    selectedSize = size,
                    selectedColor = color,
                    quantity = 1
                )
                _cartItems.update { it + newItem }
                addedCartId = newItem.id
            }
        }

        _swipeHistory.update { it + SwipeAction.Liked(product, addedCartId != null, addedCartId) }
        _currentDeckIndex.update { it + 1 }
        return true
    }

    fun swipeLeft(product: Product) {
        _swipeHistory.update { it + SwipeAction.Passed(product) }
        _currentDeckIndex.update { it + 1 }
    }

    fun undoLastSwipe(): Boolean {
        val history = _swipeHistory.value
        if (history.isEmpty() || _currentDeckIndex.value <= 0) return false

        val lastAction = history.last()
        _swipeHistory.update { it.dropLast(1) }
        _currentDeckIndex.update { (it - 1).coerceAtLeast(0) }

        when (lastAction) {
            is SwipeAction.Liked -> {
                _likedProducts.update { current -> current.filter { it.id != lastAction.product.id } }
                if (lastAction.cartItemId != null) {
                    _cartItems.update { list -> list.filter { it.id != lastAction.cartItemId } }
                }
            }
            is SwipeAction.Passed -> {
                // Deck index decremented is sufficient
            }
        }
        return true
    }

    fun resetDeck() {
        _currentDeckIndex.value = 0
    }

    fun toggleLike(product: Product) {
        _likedProducts.update { current ->
            if (current.any { it.id == product.id }) {
                current.filter { it.id != product.id }
            } else {
                current + product
            }
        }
    }

    fun isProductLiked(productId: String): Boolean {
        return _likedProducts.value.any { it.id == productId }
    }

    fun addToCart(product: Product, size: String, color: ProductColor, quantity: Int = 1) {
        val existing = _cartItems.value.find {
            it.product.id == product.id && it.selectedSize == size && it.selectedColor.name == color.name
        }
        if (existing != null) {
            _cartItems.update { list ->
                list.map { if (it.id == existing.id) it.copy(quantity = it.quantity + quantity) else it }
            }
        } else {
            val newItem = CartItem(
                id = "cart_${UUID.randomUUID().toString().take(8)}",
                product = product,
                selectedSize = size,
                selectedColor = color,
                quantity = quantity
            )
            _cartItems.update { it + newItem }
        }
    }

    fun updateCartQuantity(itemId: String, delta: Int) {
        _cartItems.update { list ->
            list.mapNotNull { item ->
                if (item.id == itemId) {
                    val newQty = item.quantity + delta
                    if (newQty > 0) item.copy(quantity = newQty) else null
                } else {
                    item
                }
            }
        }
    }

    fun removeFromCart(itemId: String) {
        _cartItems.update { list -> list.filter { it.id != itemId } }
    }

    fun clearCart() {
        _cartItems.value = emptyList()
    }

    fun applyCoupon(code: String): Boolean {
        return if (code.trim().equals("FITBUNK100", ignoreCase = true) ||
            code.trim().equals("DRIP50", ignoreCase = true)
        ) {
            _appliedCoupon.value = code.uppercase().trim()
            true
        } else {
            false
        }
    }

    fun removeCoupon() {
        _appliedCoupon.value = null
    }

    fun getCouponDiscount(subtotal: Int): Int {
        return when (_appliedCoupon.value) {
            "FITBUNK100" -> if (subtotal >= 999) 100 else 0
            "DRIP50" -> if (subtotal >= 499) 50 else 0
            else -> 0
        }
    }

    fun placeOrder(paymentMethod: String, address: DeliveryAddress): Order {
        val items = _cartItems.value
        val subtotal = items.sumOf { it.product.price * it.quantity }
        val discount = getCouponDiscount(subtotal)
        val deliveryFee = if (subtotal >= 999) 0 else 79
        val total = (subtotal - discount + deliveryFee).coerceAtLeast(0)

        val newOrder = Order(
            id = "FB-${(10000..99999).random()}",
            items = items,
            subtotal = subtotal,
            discountAmount = discount,
            deliveryFee = deliveryFee,
            totalAmount = total,
            deliveryAddress = address,
            paymentMethod = paymentMethod,
            status = OrderStatus.PLACED,
            timestamp = System.currentTimeMillis()
        )

        _orders.update { listOf(newOrder) + it }
        clearCart()
        return newOrder
    }

    fun advanceDemoOrderStatus(orderId: String) {
        _orders.update { list ->
            list.map { order ->
                if (order.id == orderId) {
                    val nextStatus = when (order.status) {
                        OrderStatus.PLACED -> OrderStatus.CONFIRMED
                        OrderStatus.CONFIRMED -> OrderStatus.PACKED
                        OrderStatus.PACKED -> OrderStatus.OUT_FOR_DELIVERY
                        OrderStatus.OUT_FOR_DELIVERY -> OrderStatus.DELIVERED
                        OrderStatus.DELIVERED -> OrderStatus.DELIVERED
                    }
                    order.copy(status = nextStatus)
                } else order
            }
        }
    }

    fun updateUserProfile(
        name: String,
        email: String,
        ageConfirmed: Boolean,
        categories: Set<ProductCategory>,
        style: StyleTag,
        budget: String
    ) {
        _userProfile.update {
            it.copy(
                name = name,
                email = email,
                ageConfirmed = ageConfirmed,
                preferredCategories = categories,
                preferredStyle = style,
                budgetPreference = budget
            )
        }
    }

    fun updateSizes(tops: String, bottoms: String, shoes: String) {
        _userProfile.update {
            it.copy(
                sizeTops = tops,
                sizeBottoms = bottoms,
                sizeShoes = shoes
            )
        }
    }

    fun addAddress(address: DeliveryAddress) {
        _userProfile.update { profile ->
            val updated = profile.addresses + address
            profile.copy(
                addresses = updated,
                selectedAddressId = address.id
            )
        }
    }

    fun selectAddress(id: String) {
        _userProfile.update { it.copy(selectedAddressId = id) }
    }

    fun updateNotificationPreferences(push: Boolean, orders: Boolean, drops: Boolean) {
        _userProfile.update {
            it.copy(
                pushNotificationsEnabled = push,
                orderAlertsEnabled = orders,
                dropAlertsEnabled = drops
            )
        }
    }
}

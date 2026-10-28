package com.example.ui.screens

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.ai.GeminiTryOnService
import com.example.ai.TryOnUiState
import com.example.model.Product
import com.example.model.ProductColor
import com.example.ui.components.FitbunkImage
import com.example.ui.components.FitbunkLogo
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class TryOnAvatar(
    val id: String,
    val name: String,
    val tagline: String,
    val height: String,
    val build: String,
    val imageUrl: String
)

enum class TryOnAngle(val label: String) {
    FRONT("Front View"),
    THREE_QUARTER("3/4 Angle"),
    SIDE("Side Profile")
}

enum class LightingMode(val label: String, val overlayColor: Color) {
    NEON_VIOLET("Neon Street", Color(0x25A855F7)),
    STUDIO_WHITE("Studio Clean", Color(0x05FFFFFF)),
    CYBER_NIGHT("Cyber Glow", Color(0x281F0D3D))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VirtualTryOnScreen(
    initialProduct: Product,
    allProducts: List<Product>,
    userDefaultSize: String,
    onBackClick: () -> Unit,
    onAddToCart: (Product, String, ProductColor) -> Unit,
    onNavigateToCart: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Initialize Gemini AI Try-On service
    val tryOnService = remember { GeminiTryOnService(context) }
    val tryOnState by tryOnService.uiState.collectAsStateWithLifecycle()

    // Preset Streetwear models
    val presetAvatars = remember {
        listOf(
            TryOnAvatar(
                id = "marcus",
                name = "Marcus",
                tagline = "Oversized Street Frame",
                height = "6'1\"",
                build = "Athletic Drop-Shoulder",
                imageUrl = "https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?w=600&auto=format&fit=crop&q=80"
            ),
            TryOnAvatar(
                id = "priya",
                name = "Priya",
                tagline = "Cropped & Cargo Silhouette",
                height = "5'6\"",
                build = "Slim Street Drape",
                imageUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=600&auto=format&fit=crop&q=80"
            ),
            TryOnAvatar(
                id = "leo",
                name = "Leo",
                tagline = "Boxy Techwear Cut",
                height = "5'10\"",
                build = "Medium Broad Shoulders",
                imageUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=600&auto=format&fit=crop&q=80"
            ),
            TryOnAvatar(
                id = "maya",
                name = "Maya",
                tagline = "Relaxed Street Fit",
                height = "5'8\"",
                build = "Bespoke Streetwear",
                imageUrl = "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=600&auto=format&fit=crop&q=80"
            )
        )
    }

    var selectedProduct by remember { mutableStateOf(initialProduct) }
    var customGarmentUri by remember { mutableStateOf<Uri?>(null) }
    var selectedAvatar by remember { mutableStateOf(presetAvatars[0]) }
    var userCustomPhotoUri by remember { mutableStateOf<Uri?>(null) }

    var selectedSize by remember {
        mutableStateOf(if (userDefaultSize.isNotBlank()) userDefaultSize else selectedProduct.sizes.firstOrNull() ?: "L")
    }
    var selectedColor by remember {
        mutableStateOf(selectedProduct.colors.firstOrNull() ?: ProductColor("Black", 0xFF000000))
    }
    var currentAngle by remember { mutableStateOf(TryOnAngle.FRONT) }
    var currentLighting by remember { mutableStateOf(LightingMode.NEON_VIOLET) }
    var showOriginalComparison by remember { mutableStateOf(false) }
    var outfitSwitchBottomSheetVisible by remember { mutableStateOf(false) }

    // User Photo picker launcher
    val userPhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            userCustomPhotoUri = uri
            tryOnService.resetState()
        }
    }

    // Custom Garment picker launcher (so user can upload any clothes to try on!)
    val garmentPhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            customGarmentUri = uri
            tryOnService.resetState()
        }
    }

    // Scanning laser animation
    val infiniteTransition = rememberInfiniteTransition(label = "laser_transition")
    val laserOffsetY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "laser_anim"
    )

    // Fit recommendation text
    val fitRecommendation = remember(selectedProduct.category, selectedSize) {
        when (selectedSize) {
            "L" -> "Signature FITBUNK silhouette. Drop shoulders rest 1.5\" past the acromion for relaxed draping."
            "M" -> "Tailored streetwear drape. Sits closer to the torso while maintaining easy arm mobility."
            "XL" -> "Deep relaxed street fit. Extended sleeve cuffs stack gracefully over watches and rings."
            "S" -> "Structured snug fit. Ideal for layering under heavy winter windbreakers."
            else -> "Comfortable oversized cut with heavy street drape."
        }
    }

    Scaffold(
        topBar = {
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
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconButton(
                            onClick = onBackClick,
                            modifier = Modifier
                                .size(40.dp)
                                .testTag("try_on_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = FitbunkDeepPurple
                            )
                        }

                        FitbunkLogo(size = 32.dp)

                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "GEMINI AI TRY-ON",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Black,
                                    color = FitbunkDeepPurple
                                )
                                Icon(
                                    imageVector = Icons.Filled.AutoAwesome,
                                    contentDescription = null,
                                    tint = FitbunkViolet,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Text(
                                text = "POWERED BY GEMINI AI",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = FitbunkViolet,
                                letterSpacing = 1.sp
                            )
                        }
                    }

                    // Change product button
                    OutlinedButton(
                        onClick = { outfitSwitchBottomSheetVisible = true },
                        modifier = Modifier
                            .height(36.dp)
                            .testTag("switch_outfit_button"),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        border = BorderStroke(1.dp, FitbunkViolet)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Checkroom,
                            contentDescription = null,
                            tint = FitbunkViolet,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Switch Fit",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = FitbunkViolet
                        )
                    }
                }
            }
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = FitbunkWhite,
                shadowElevation = 16.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = selectedProduct.name,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = FitbunkTextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.widthIn(max = 180.dp)
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "₹${selectedProduct.price}",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                color = FitbunkDeepPurple
                            )
                            Text(
                                text = "₹${selectedProduct.originalPrice}",
                                fontSize = 13.sp,
                                color = FitbunkTextSecondary,
                                style = androidx.compose.ui.text.TextStyle(
                                    textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough
                                )
                            )
                        }
                    }

                    Button(
                        onClick = {
                            onAddToCart(selectedProduct, selectedSize, selectedColor)
                        },
                        modifier = Modifier
                            .height(50.dp)
                            .testTag("try_on_add_to_cart_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = FitbunkDeepPurple),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(Icons.Filled.ShoppingBag, contentDescription = null, tint = FitbunkWhite)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "ADD TO CART",
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp,
                            color = FitbunkWhite
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .background(FitbunkLightBg)
        ) {
            // 1. THE VIRTUAL TRY-ON CANVAS
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(440.dp)
                    .background(Color(0xFF0F071A))
            ) {
                when (val state = tryOnState) {
                    is TryOnUiState.Success -> {
                        // Render the real Gemini AI Generated Image or Original comparison
                        if (showOriginalComparison) {
                            Image(
                                bitmap = state.originalUserBitmap.asImageBitmap(),
                                contentDescription = "Original Photo",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Image(
                                bitmap = state.resultBitmap.asImageBitmap(),
                                contentDescription = "Gemini AI Clothes Swap Result",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }

                    is TryOnUiState.Loading -> {
                        // Show base photo with real-time AI scanning effect
                        if (userCustomPhotoUri != null) {
                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(userCustomPhotoUri)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Person Photo",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(selectedAvatar.imageUrl)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = selectedAvatar.name,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }

                        // Laser Scanner Animation during AI Synthesis
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(laserOffsetY)
                                .background(
                                    Brush.verticalGradient(
                                        listOf(
                                            Color.Transparent,
                                            FitbunkViolet.copy(alpha = 0.35f),
                                            FitbunkLavender.copy(alpha = 0.95f)
                                        )
                                    )
                                )
                        )

                        // Loading HUD Center Overlay
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color(0x99000000)),
                            contentAlignment = Alignment.Center
                        ) {
                            Card(
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xEE1E0B36)),
                                border = BorderStroke(1.5.dp, FitbunkViolet),
                                modifier = Modifier.padding(24.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(20.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    CircularProgressIndicator(
                                        color = FitbunkLavender,
                                        modifier = Modifier.size(44.dp),
                                        strokeWidth = 3.dp
                                    )
                                    Spacer(modifier = Modifier.height(14.dp))
                                    Text(
                                        text = "GEMINI AI GENERATING",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 14.sp,
                                        color = FitbunkWhite,
                                        letterSpacing = 1.sp
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = state.stage,
                                        fontSize = 12.sp,
                                        color = FitbunkLavender,
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    LinearProgressIndicator(
                                        progress = { state.progress },
                                        color = FitbunkViolet,
                                        trackColor = Color(0x33FFFFFF),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(6.dp)
                                            .clip(RoundedCornerShape(3.dp))
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Tailoring real fabric folds & drape (takes ~30-45s)...",
                                        fontSize = 10.sp,
                                        color = FitbunkTextSecondary,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }

                    is TryOnUiState.Error -> {
                        // Show base photo with Error HUD
                        if (userCustomPhotoUri != null) {
                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(userCustomPhotoUri)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Person Photo",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(selectedAvatar.imageUrl)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = selectedAvatar.name,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color(0xBB000000)),
                            contentAlignment = Alignment.Center
                        ) {
                            Card(
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF220A1E)),
                                border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.error),
                                modifier = Modifier.padding(24.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(18.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Warning,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(36.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Virtual Try-On Issue",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = FitbunkWhite
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = state.message,
                                        fontSize = 12.sp,
                                        color = FitbunkTextSecondary,
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(14.dp))
                                    Button(
                                        onClick = {
                                            coroutineScope.launch {
                                                tryOnService.executeTryOn(
                                                    userPhotoUri = userCustomPhotoUri,
                                                    avatarUrl = selectedAvatar.imageUrl,
                                                    productImageUrl = selectedProduct.primaryImage,
                                                    productName = selectedProduct.name,
                                                    selectedSize = selectedSize,
                                                    productBrand = selectedProduct.brand,
                                                    customProductUri = customGarmentUri,
                                                    angle = currentAngle,
                                                    lighting = currentLighting
                                                )
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = FitbunkViolet),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Icon(Icons.Filled.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Retry Swap")
                                    }
                                }
                            }
                        }
                    }

                    is TryOnUiState.Idle -> {
                        // Clean, uncorrupted photo of the person/model
                        if (userCustomPhotoUri != null) {
                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(userCustomPhotoUri)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Your Photo",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(selectedAvatar.imageUrl)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = selectedAvatar.name,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }

                        // Floating Garment Preview Badge in bottom-left corner
                        Surface(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(12.dp),
                            shape = RoundedCornerShape(14.dp),
                            color = Color(0xDD180A2E),
                            border = BorderStroke(1.dp, FitbunkViolet)
                        ) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                if (customGarmentUri != null) {
                                    AsyncImage(
                                        model = customGarmentUri,
                                        contentDescription = "Custom Garment",
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clip(RoundedCornerShape(8.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    FitbunkImage(
                                        imageUrl = selectedProduct.primaryImage,
                                        contentDescription = selectedProduct.name,
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                    )
                                }
                                Column {
                                    Text(
                                        text = "Garment Ready",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = FitbunkLavender
                                    )
                                    Text(
                                        text = if (customGarmentUri != null) "Uploaded Cloth" else selectedProduct.name,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = FitbunkWhite,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.widthIn(max = 140.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Studio Lighting Gradient Overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(currentLighting.overlayColor)
                )

                // Top Controls over Canvas
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                        .align(Alignment.TopCenter),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // AI Status Badge
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xCC180A2E),
                        border = BorderStroke(1.dp, FitbunkViolet)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(if (tryOnState is TryOnUiState.Success) FitbunkAccentGreen else FitbunkViolet)
                            )
                            Text(
                                text = if (tryOnState is TryOnUiState.Success) "AI Fitted ✨" else "Ready to Fit",
                                color = FitbunkWhite,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Before / After Toggle (Active if AI Try-On succeeded)
                    if (tryOnState is TryOnUiState.Success) {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (showOriginalComparison) FitbunkViolet else Color(0xCC180A2E),
                            border = BorderStroke(1.dp, FitbunkLavender),
                            modifier = Modifier
                                .clickable { showOriginalComparison = !showOriginalComparison }
                                .testTag("before_after_toggle_btn")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = if (showOriginalComparison) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                    contentDescription = null,
                                    tint = FitbunkWhite,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = if (showOriginalComparison) "Original" else "Gemini Fit",
                                    color = FitbunkWhite,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // Bottom Overlay Bar on Canvas (Angle selector & Lighting toggle)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, Color(0xDD000000))
                            )
                        )
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        TryOnAngle.values().forEach { angle ->
                            val isSelected = currentAngle == angle
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) FitbunkViolet else Color(0x44FFFFFF),
                                modifier = Modifier
                                    .clickable { currentAngle = angle }
                                    .testTag("angle_${angle.name}")
                            ) {
                                Text(
                                    text = angle.label,
                                    color = FitbunkWhite,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    // Lighting mode cycle button
                    IconButton(
                        onClick = {
                            val nextIndex = (currentLighting.ordinal + 1) % LightingMode.values().size
                            currentLighting = LightingMode.values()[nextIndex]
                        },
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Color(0x88FFFFFF))
                            .testTag("toggle_lighting_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.WbSunny,
                            contentDescription = "Change Lighting",
                            tint = FitbunkWhite,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // PRIMARY ACTION: TRIGGER GEMINI AI CLOTHES SWAP
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
                    .shadow(8.dp, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = FitbunkDeepPurple)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AutoAwesome,
                            contentDescription = null,
                            tint = FitbunkLavender,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "GEMINI AI CLOTHES SWAP",
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp,
                            color = FitbunkWhite,
                            letterSpacing = 0.5.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Upload your photo and product image. Gemini AI realistically changes the clothes onto your body.",
                        fontSize = 11.sp,
                        color = FitbunkLavenderLight,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            coroutineScope.launch {
                                tryOnService.executeTryOn(
                                    userPhotoUri = userCustomPhotoUri,
                                    avatarUrl = selectedAvatar.imageUrl,
                                    productImageUrl = selectedProduct.primaryImage,
                                    productName = selectedProduct.name,
                                    selectedSize = selectedSize,
                                    productBrand = selectedProduct.brand,
                                    customProductUri = customGarmentUri,
                                    angle = currentAngle,
                                    lighting = currentLighting
                                )
                            }
                        },
                        enabled = tryOnState !is TryOnUiState.Loading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("gemini_try_on_btn"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = FitbunkViolet,
                            disabledContainerColor = FitbunkViolet.copy(alpha = 0.5f)
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        if (tryOnState is TryOnUiState.Loading) {
                            CircularProgressIndicator(
                                color = FitbunkWhite,
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "SWAPPING CLOTHES...",
                                fontWeight = FontWeight.Black,
                                fontSize = 13.sp,
                                color = FitbunkWhite
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Filled.FlashOn,
                                contentDescription = null,
                                tint = FitbunkAccentGold,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (tryOnState is TryOnUiState.Success) "RE-GENERATE FIT WITH GEMINI" else "SWAP CLOTHES WITH GEMINI AI",
                                fontWeight = FontWeight.Black,
                                fontSize = 13.sp,
                                color = FitbunkWhite
                            )
                        }
                    }
                }
            }

            // 2. PERSON PHOTO SELECTION (Upload You or Choose Preset Avatar)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = FitbunkWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "STEP 1: YOUR PHOTO / MODEL",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                color = FitbunkDeepPurple
                            )
                            Text(
                                text = if (userCustomPhotoUri != null) "Using your uploaded photo" else "Using ${selectedAvatar.name}",
                                fontSize = 11.sp,
                                color = FitbunkTextSecondary
                            )
                        }

                        Button(
                            onClick = {
                                userPhotoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = FitbunkViolet),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("upload_user_photo_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.AddPhotoAlternate,
                                contentDescription = null,
                                tint = FitbunkWhite,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (userCustomPhotoUri != null) "Change Photo" else "Upload You",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = FitbunkWhite
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // User uploaded photo chip
                        if (userCustomPhotoUri != null) {
                            item {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .border(2.dp, FitbunkViolet, RoundedCornerShape(12.dp))
                                        .background(FitbunkLavenderLight)
                                        .padding(6.dp)
                                        .testTag("selected_user_avatar")
                                ) {
                                    AsyncImage(
                                        model = ImageRequest.Builder(context)
                                            .data(userCustomPhotoUri)
                                            .crossfade(true)
                                            .build(),
                                        contentDescription = "Your Photo",
                                        modifier = Modifier
                                            .size(54.dp)
                                            .clip(CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "You ✨",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = FitbunkDeepPurple
                                    )
                                }
                            }
                        }

                        // Preset Models
                        items(presetAvatars) { avatar ->
                            val isSelected = userCustomPhotoUri == null && selectedAvatar.id == avatar.id
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) FitbunkViolet else FitbunkCardBorder,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .background(if (isSelected) FitbunkLavenderLight else FitbunkWhite)
                                    .clickable {
                                        userCustomPhotoUri = null
                                        selectedAvatar = avatar
                                        tryOnService.resetState()
                                    }
                                    .padding(8.dp)
                                    .testTag("avatar_chip_${avatar.id}")
                            ) {
                                AsyncImage(
                                    model = ImageRequest.Builder(context)
                                        .data(avatar.imageUrl)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = avatar.name,
                                    modifier = Modifier
                                        .size(54.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = avatar.name,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) FitbunkDeepPurple else FitbunkTextPrimary
                                )
                                Text(
                                    text = avatar.height,
                                    fontSize = 10.sp,
                                    color = FitbunkTextSecondary
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 3. PRODUCT OR CUSTOM GARMENT UPLOAD
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = FitbunkWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "STEP 2: GARMENT TO TRY ON",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                color = FitbunkDeepPurple
                            )
                            Text(
                                text = if (customGarmentUri != null) "Custom uploaded garment" else selectedProduct.name,
                                fontSize = 11.sp,
                                color = FitbunkTextSecondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.widthIn(max = 200.dp)
                            )
                        }

                        // Upload custom garment button
                        OutlinedButton(
                            onClick = {
                                garmentPhotoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            border = BorderStroke(1.dp, FitbunkViolet),
                            modifier = Modifier.testTag("upload_custom_garment_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.UploadFile,
                                contentDescription = null,
                                tint = FitbunkViolet,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (customGarmentUri != null) "Change Cloth" else "Upload Cloth",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = FitbunkViolet
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Selected item row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(FitbunkLavenderLight)
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (customGarmentUri != null) {
                            AsyncImage(
                                model = customGarmentUri,
                                contentDescription = "Custom Garment",
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(RoundedCornerShape(10.dp)),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            FitbunkImage(
                                imageUrl = selectedProduct.primaryImage,
                                contentDescription = selectedProduct.name,
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(RoundedCornerShape(10.dp))
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (customGarmentUri != null) "CUSTOM GARMENT PHOTO" else selectedProduct.brand.uppercase(),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = FitbunkViolet
                            )
                            Text(
                                text = if (customGarmentUri != null) "Uploaded Clothes Item" else selectedProduct.name,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = FitbunkTextPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = if (customGarmentUri != null) "Ready for Gemini Try-On" else "₹${selectedProduct.price} • ${selectedProduct.discountPercent}% OFF",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = FitbunkDeepPurple
                            )
                        }

                        TextButton(
                            onClick = {
                                customGarmentUri = null
                                outfitSwitchBottomSheetVisible = true
                            },
                            modifier = Modifier.testTag("browse_catalog_garments_btn")
                        ) {
                            Text(
                                text = "Browse 1,000+",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = FitbunkViolet
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 4. SIZE & DRAPE SELECTOR
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = FitbunkWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "CHOOSE SIZE FOR FIT TAILORING",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = FitbunkDeepPurple
                        )

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = FitbunkLavenderLight
                        ) {
                            Text(
                                text = "✨ AI Recommended: Size L",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = FitbunkDeepPurple,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("S", "M", "L", "XL", "XXL").forEach { size ->
                            val isSelected = selectedSize == size
                            val isAvailable = selectedProduct.sizes.contains(size)

                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable(enabled = isAvailable) {
                                        selectedSize = size
                                    }
                                    .testTag("try_on_size_$size"),
                                shape = RoundedCornerShape(10.dp),
                                color = when {
                                    isSelected -> FitbunkDeepPurple
                                    !isAvailable -> FitbunkCardBorder.copy(alpha = 0.4f)
                                    else -> FitbunkLavenderLight
                                },
                                border = if (isSelected) BorderStroke(1.5.dp, FitbunkViolet) else null
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = size,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = when {
                                            isSelected -> FitbunkWhite
                                            !isAvailable -> FitbunkTextSecondary.copy(alpha = 0.5f)
                                            else -> FitbunkDeepPurple
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 5. AI FIT INTELLIGENCE & STYLING VERDICT
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = FitbunkWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(FitbunkViolet),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.AutoAwesome,
                                contentDescription = null,
                                tint = FitbunkWhite,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "FITBUNK AI™ Fit & Styling Verdict",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = FitbunkDeepPurple
                            )
                            Text(
                                text = "Trained on streetwear silhouette geometry",
                                fontSize = 11.sp,
                                color = FitbunkTextSecondary
                            )
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = FitbunkCardBorder)

                    // Drape analysis
                    Row(
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = FitbunkAccentGreen,
                            modifier = Modifier.size(18.dp)
                        )
                        Column {
                            Text(
                                text = "Drape & Shoulder Alignment",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = FitbunkTextPrimary
                            )
                            Text(
                                text = if (tryOnState is TryOnUiState.Success) {
                                    (tryOnState as TryOnUiState.Success).styleNotes
                                } else {
                                    fitRecommendation
                                },
                                fontSize = 12.sp,
                                color = FitbunkTextSecondary,
                                lineHeight = 16.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Styling Synergy
                    Row(
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Style,
                            contentDescription = null,
                            tint = FitbunkViolet,
                            modifier = Modifier.size(18.dp)
                        )
                        Column {
                            Text(
                                text = "Streetwear Styling Match",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = FitbunkTextPrimary
                            )
                            Text(
                                text = "Pairs exceptionally with Parachute Cargo Pants and High-Top Chunky Trainers. Color harmony score: 9.8/10.",
                                fontSize = 12.sp,
                                color = FitbunkTextSecondary,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }

    // BOTTOM SHEET FOR SWITCHING FITS FROM 1,000 PRODUCT INVENTORY
    if (outfitSwitchBottomSheetVisible) {
        ModalBottomSheet(
            onDismissRequest = { outfitSwitchBottomSheetVisible = false },
            containerColor = FitbunkWhite,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "CHOOSE PIECE FROM INVENTORY (1,000+)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = FitbunkDeepPurple
                    )
                    IconButton(onClick = { outfitSwitchBottomSheetVisible = false }) {
                        Icon(Icons.Filled.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp)
                ) {
                    items(allProducts.take(40)) { prod ->
                        val isChosen = prod.id == selectedProduct.id && customGarmentUri == null
                        Card(
                            modifier = Modifier
                                .width(130.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .clickable {
                                    selectedProduct = prod
                                    customGarmentUri = null
                                    selectedSize = prod.sizes.firstOrNull() ?: "L"
                                    outfitSwitchBottomSheetVisible = false
                                    tryOnService.resetState()
                                }
                                .testTag("picker_outfit_${prod.id}"),
                            colors = CardDefaults.cardColors(containerColor = if (isChosen) FitbunkLavenderLight else FitbunkWhite),
                            border = if (isChosen) BorderStroke(2.dp, FitbunkViolet) else BorderStroke(1.dp, FitbunkCardBorder)
                        ) {
                            Column {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(110.dp)
                                ) {
                                    FitbunkImage(
                                        imageUrl = prod.primaryImage,
                                        contentDescription = prod.name,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text(
                                        text = prod.brand,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = FitbunkViolet
                                    )
                                    Text(
                                        text = prod.name,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = FitbunkTextPrimary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "₹${prod.price}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Black,
                                        color = FitbunkDeepPurple
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onTimeout: () -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }
    var progress by remember { mutableStateOf(0f) }

    // Pulsating ambient neon aura
    val infiniteTransition = rememberInfiniteTransition(label = "splash_ambient_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.80f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(2200, easing = LinearEasing),
        label = "splash_progress"
    )

    LaunchedEffect(Unit) {
        isVisible = true
        progress = 1f
        // Automatic transition to Welcome screen after 2.3 seconds
        delay(2300)
        onTimeout()
    }

    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1B072D),
                        FitbunkDeepPurple,
                        Color(0xFF150524)
                    )
                )
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onTimeout() } // Instant tap anywhere to skip directly
            .testTag("splash_screen_root"),
        contentAlignment = Alignment.Center
    ) {
        // Outer Ambient Radial Glow
        Box(
            modifier = Modifier
                .size(380.dp)
                .scale(pulseScale)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            FitbunkViolet.copy(alpha = glowAlpha),
                            FitbunkLavender.copy(alpha = glowAlpha * 0.4f),
                            Color.Transparent
                        )
                    )
                )
        )

        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(tween(700)) + scaleIn(tween(700, easing = FastOutSlowInEasing), initialScale = 0.82f)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                // Circular FITBUNK Brand Logo with layered neon halo
                Box(
                    modifier = Modifier
                        .size(164.dp)
                        .scale(pulseScale)
                        .shadow(elevation = 24.dp, shape = CircleShape, spotColor = FitbunkViolet, ambientColor = FitbunkLavender)
                        .clip(CircleShape)
                        .background(Color(0xFF2A0D4A))
                        .border(
                            width = 3.dp,
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    FitbunkLavender,
                                    FitbunkViolet,
                                    FitbunkLavender.copy(alpha = 0.6f)
                                )
                            ),
                            shape = CircleShape
                        )
                        .testTag("splash_fb_logo"),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.fitbunk_logo_circle),
                        contentDescription = "FITBUNK Official Logo",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Brand Name: FITBUNK
                Text(
                    text = "FITBUNK",
                    color = FitbunkWhite,
                    fontWeight = FontWeight.Black,
                    fontSize = 42.sp,
                    letterSpacing = 3.sp,
                    modifier = Modifier.testTag("splash_brand_title")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Brand Tagline: "FASHION, WITHOUT THE WAIT."
                Text(
                    text = "FASHION, WITHOUT THE WAIT.",
                    color = FitbunkLavender,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    letterSpacing = 2.5.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.testTag("splash_brand_tagline")
                )

                Spacer(modifier = Modifier.height(44.dp))

                // Loading progress indicator
                Column(
                    modifier = Modifier.width(220.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    LinearProgressIndicator(
                        progress = { animatedProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp)),
                        color = FitbunkLavender,
                        trackColor = FitbunkSurfaceElevated
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = null,
                            tint = FitbunkAccentGold,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Initializing Streetwear Drops...",
                            color = FitbunkLavenderLight,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // Tap to continue hint
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 32.dp)
                .clip(RoundedCornerShape(24.dp))
                .clickable { onTimeout() }
                .testTag("splash_skip_trigger"),
            color = Color(0xFF280E44).copy(alpha = 0.85f),
            shape = RoundedCornerShape(24.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, FitbunkLavender.copy(alpha = 0.35f))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 9.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "TAP TO ENTER",
                    color = FitbunkWhite,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = FitbunkLavender,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.ui.components.FitbunkLogo
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onTimeout: () -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }
    var progress by remember { mutableStateOf(0f) }

    // Pulsating ambient halo
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.75f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(1800, easing = LinearEasing),
        label = "splash_progress"
    )

    LaunchedEffect(Unit) {
        isVisible = true
        progress = 1f
        // Automatic transition trigger to Welcome screen after 1.9s
        delay(1900)
        onTimeout()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(FitbunkDeepPurple)
            .clickable { onTimeout() } // Instant tap trigger to skip directly to Welcome
            .testTag("splash_screen_root"),
        contentAlignment = Alignment.Center
    ) {
        // Ambient background glow
        Box(
            modifier = Modifier
                .size(340.dp)
                .scale(pulseScale)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            FitbunkViolet.copy(alpha = glowAlpha),
                            FitbunkDeepPurple.copy(alpha = 0f)
                        )
                    )
                )
        )

        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(tween(600)) + scaleIn(tween(600, easing = FastOutSlowInEasing), initialScale = 0.88f)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(24.dp)
            ) {
                // Circular FITBUNK Brand Logo (Official Emblem)
                Box(
                    modifier = Modifier
                        .size(130.dp)
                        .scale(pulseScale)
                        .clip(CircleShape)
                        .border(3.dp, FitbunkLavender.copy(alpha = 0.85f), CircleShape)
                        .testTag("splash_fb_logo"),
                    contentAlignment = Alignment.Center
                ) {
                    FitbunkLogo(size = 130.dp)
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Brand Name: FITBUNK
                Text(
                    text = "FITBUNK",
                    color = FitbunkWhite,
                    fontWeight = FontWeight.Black,
                    fontSize = 40.sp,
                    letterSpacing = 2.5.sp,
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
                            text = "Initializing 60m Drop Network",
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
                .padding(bottom = 28.dp)
                .clip(RoundedCornerShape(20.dp))
                .clickable { onTimeout() }
                .testTag("splash_skip_trigger"),
            color = FitbunkSurfaceDark.copy(alpha = 0.8f),
            shape = RoundedCornerShape(20.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, FitbunkLavender.copy(alpha = 0.25f))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "TAP TO CONTINUE",
                    color = FitbunkLavenderLight,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = FitbunkLavenderLight,
                    modifier = Modifier.size(13.dp)
                )
            }
        }
    }
}

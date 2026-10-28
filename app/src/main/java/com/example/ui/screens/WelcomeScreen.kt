package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.FitbunkImage
import com.example.ui.components.FitbunkLogo
import com.example.ui.theme.*

@Composable
fun WelcomeScreen(
    onLetsGoClick: () -> Unit,
    onAlreadyAccountClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(FitbunkDeepPurple)
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("welcome_screen_root")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header: FITBUNK circular logo and title
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                FitbunkLogo(size = 40.dp)

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = "FITBUNK",
                    color = FitbunkWhite,
                    fontWeight = FontWeight.Black,
                    fontSize = 22.sp,
                    letterSpacing = 1.5.sp
                )
            }

            // Central Fashion Visual Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 20.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .border(1.5.dp, FitbunkViolet.copy(alpha = 0.5f), RoundedCornerShape(28.dp))
                    .shadow(12.dp)
            ) {
                FitbunkImage(
                    imageUrl = "https://images.unsplash.com/photo-1515886657613-9f3515b0c78f?auto=format&fit=crop&w=1000&q=80",
                    contentDescription = "Fitbunk Streetwear Editorial Look",
                    modifier = Modifier.fillMaxSize()
                )

                // Dark gradient overlay for text readability
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    FitbunkDeepPurple.copy(alpha = 0.4f),
                                    FitbunkDeepPurple.copy(alpha = 0.95f)
                                )
                            )
                        )
                )

                // Feature Pill overlays
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = FitbunkViolet.copy(alpha = 0.9f)
                    ) {
                        Text(
                            text = "⚡ Hyperfast 60-Minute Outfits",
                            color = FitbunkWhite,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = FitbunkDeepPurple.copy(alpha = 0.85f)
                    ) {
                        Text(
                            text = "🔥 Tinder-Style Fit Discovery",
                            color = FitbunkLavender,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            // Bottom Content: Headlines and Actions
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Your next fit awaits.",
                    color = FitbunkWhite,
                    fontWeight = FontWeight.Black,
                    fontSize = 32.sp,
                    lineHeight = 38.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.testTag("welcome_headline")
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Discover styles you love. Swipe your way to a better wardrobe delivered in 60 mins.",
                    color = FitbunkLavender,
                    fontSize = 15.sp,
                    lineHeight = 22.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(28.dp))

                // Primary Button: "LET'S GO"
                Button(
                    onClick = onLetsGoClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("welcome_lets_go_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FitbunkViolet
                    ),
                    shape = RoundedCornerShape(16.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "LET'S GO",
                            color = FitbunkWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = FitbunkWhite
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Secondary Option: "I already have an account"
                Text(
                    text = "I already have an account",
                    color = FitbunkLavenderLight,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(onClick = onAlreadyAccountClick)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .testTag("welcome_already_account_link")
                )
            }
        }
    }
}

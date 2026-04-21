package com.dwa.fridgepharmacy.feature.onboarding.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dwa.fridgepharmacy.core.ui.theme.AppColors
import kotlinx.coroutines.launch

private data class OnboardingPage(
    val icon: String,
    val title: String,
    val description: String,
    val accentColor: Color
)

private val pages = listOf(
    OnboardingPage(
        icon = "\uD83D\uDC8A",
        title = "Track Your Medications",
        description = "Keep a complete inventory of every medication in your home fridge. Add names, quantities, expiry dates, and storage requirements — all in one place.",
        accentColor = AppColors.Green
    ),
    OnboardingPage(
        icon = "\u26A0\uFE0F",
        title = "Smart Warnings",
        description = "Get instant alerts when medications expire or are expiring soon. The app also warns you about incorrect storage conditions based on your fridge temperature.",
        accentColor = AppColors.Amber
    ),
    OnboardingPage(
        icon = "\uD83D\uDCF7",
        title = "Scan Prescriptions",
        description = "Take a photo of your prescription and let the app extract medication names and quantities automatically. Review, edit, and add them to your pharmacy in seconds.",
        accentColor = AppColors.Blue
    )
)

@Composable
fun OnboardingScreen(onComplete: () -> Unit) {
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()
    val isLastPage = pagerState.currentPage == pages.lastIndex

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background)
    ) {
        // Skip button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 60.dp),
            horizontalArrangement = Arrangement.End
        ) {
            if (!isLastPage) {
                TextButton(onClick = onComplete) {
                    Text("Skip", color = AppColors.SubText, fontSize = 16.sp)
                }
            } else {
                Spacer(Modifier.height(48.dp))
            }
        }

        // Pages
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { pageIndex ->
            PageContent(pages[pageIndex])
        }

        // Bottom controls
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Page indicators
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                pages.forEachIndexed { index, _ ->
                    PageIndicator(isActive = index == pagerState.currentPage)
                }
            }

            Spacer(Modifier.height(32.dp))

            // Action button
            Button(
                onClick = {
                    if (isLastPage) {
                        onComplete()
                    } else {
                        scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppColors.Green,
                    contentColor = Color.White
                )
            ) {
                Text(
                    if (isLastPage) "Get Started" else "Next",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            }
        }
    }
}

@Composable
private fun PageContent(page: OnboardingPage) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Icon circle
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(RoundedCornerShape(32.dp))
                .background(page.accentColor.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Text(page.icon, fontSize = 52.sp)
        }

        Spacer(Modifier.height(40.dp))

        Text(
            text = page.title,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.Text,
            textAlign = TextAlign.Center,
            letterSpacing = (-0.5).sp
        )

        Spacer(Modifier.height(16.dp))

        Text(
            text = page.description,
            fontSize = 16.sp,
            color = AppColors.SubText,
            textAlign = TextAlign.Center,
            lineHeight = 24.sp
        )
    }
}

@Composable
private fun PageIndicator(isActive: Boolean) {
    val width by animateDpAsState(if (isActive) 24.dp else 8.dp)
    val color by animateColorAsState(if (isActive) AppColors.Green else AppColors.Green.copy(alpha = 0.25f))

    Box(
        modifier = Modifier
            .height(8.dp)
            .width(width)
            .clip(CircleShape)
            .background(color)
    )
}

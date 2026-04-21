package com.dwa.fridgepharmacy

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import com.dwa.fridgepharmacy.core.ui.theme.AppColors
import com.dwa.fridgepharmacy.navigation.AppNavigation

private val PharmacyColorScheme = lightColorScheme(
    primary = AppColors.Green,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    primaryContainer = AppColors.GreenLight,
    onPrimaryContainer = AppColors.Green,
    secondary = AppColors.GreenMid,
    background = AppColors.Background,
    surface = AppColors.Card,
    onBackground = AppColors.Text,
    onSurface = AppColors.Text,
    onSurfaceVariant = AppColors.SubText,
    error = AppColors.Red,
    errorContainer = AppColors.RedLight,
)

@Composable
fun App() {
    MaterialTheme(colorScheme = PharmacyColorScheme) {
        AppNavigation()
    }
}

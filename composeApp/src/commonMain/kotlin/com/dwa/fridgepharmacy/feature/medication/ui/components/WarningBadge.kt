package com.dwa.fridgepharmacy.feature.medication.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dwa.fridgepharmacy.core.ui.theme.AppColors
import com.dwa.fridgepharmacy.feature.medication.domain.MedicationWarning

@Composable
fun WarningBadge(warning: MedicationWarning, modifier: Modifier = Modifier) {
    val (text, color, bgColor) = when (warning) {
        is MedicationWarning.Expired -> Triple("EXPIRED", AppColors.Red, AppColors.RedLight)
        is MedicationWarning.ExpiresSoon -> Triple("Expires in ${warning.daysLeft}d", AppColors.Amber, AppColors.AmberLight)
        is MedicationWarning.InfoKeepRefrigerated -> Triple("Keep refrigerated", AppColors.Blue, AppColors.BlueLight)
        is MedicationWarning.ShouldNotBeRefrigerated -> Triple("Should not be refrigerated", AppColors.Amber, AppColors.AmberLight)
        is MedicationWarning.TemperatureOutOfRange ->
            Triple("Temp out of range (${warning.minRequired.toInt()}-${warning.maxRequired.toInt()}°C required)", AppColors.Amber, AppColors.AmberLight)
    }

    Surface(modifier = modifier, color = bgColor, shape = RoundedCornerShape(20.dp)) {
        Row(
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = when (warning) {
                    is MedicationWarning.Expired -> Icons.Default.Warning
                    is MedicationWarning.InfoKeepRefrigerated -> Icons.Default.Info
                    else -> Icons.Default.Warning
                },
                contentDescription = null, tint = color, modifier = Modifier.size(10.dp)
            )
            Spacer(Modifier.width(3.dp))
            Text(text, color = color, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 0.3.sp, maxLines = 1)
        }
    }
}

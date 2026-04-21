package com.dwa.fridgepharmacy.feature.medication.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dwa.fridgepharmacy.core.ui.theme.AppColors
import com.dwa.fridgepharmacy.feature.medication.domain.MedicationWarning
import com.dwa.fridgepharmacy.feature.medication.ui.list.MedicationWithWarnings

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MedicationCard(
    item: MedicationWithWarnings,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val topWarning = item.warnings.firstOrNull()
    val borderColor = when (topWarning) {
        is MedicationWarning.Expired -> AppColors.Red
        is MedicationWarning.ExpiresSoon, is MedicationWarning.ShouldNotBeRefrigerated,
        is MedicationWarning.TemperatureOutOfRange -> AppColors.Amber
        is MedicationWarning.InfoKeepRefrigerated -> AppColors.Blue
        null -> AppColors.Green.copy(alpha = 0f)
    }
    val iconBg = when (topWarning) {
        is MedicationWarning.Expired -> AppColors.RedLight
        is MedicationWarning.ExpiresSoon, is MedicationWarning.ShouldNotBeRefrigerated,
        is MedicationWarning.TemperatureOutOfRange -> AppColors.AmberLight
        is MedicationWarning.InfoKeepRefrigerated -> AppColors.BlueLight
        null -> AppColors.GreenLight
    }
    val iconColor = when (topWarning) {
        is MedicationWarning.Expired -> AppColors.Red
        is MedicationWarning.ExpiresSoon, is MedicationWarning.ShouldNotBeRefrigerated,
        is MedicationWarning.TemperatureOutOfRange -> AppColors.Amber
        is MedicationWarning.InfoKeepRefrigerated -> AppColors.Blue
        null -> AppColors.Green
    }

    Surface(modifier = modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), color = AppColors.Card, shadowElevation = 1.dp) {
        Row(modifier = Modifier.clickable(onClick = onClick).padding(start = 0.dp)) {
            Box(modifier = Modifier.width(3.dp).height(80.dp).background(borderColor))
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 15.dp), verticalAlignment = Alignment.Top) {
                Box(modifier = Modifier.size(46.dp).clip(RoundedCornerShape(12.dp)).background(iconBg), contentAlignment = Alignment.Center) {
                    Text(item.medication.name.first().uppercase(), color = iconColor, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(item.medication.name, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = AppColors.Text, letterSpacing = (-0.3).sp)
                    Text("${item.medication.quantity} · expires ${item.medication.expiryDate}", fontSize = 13.sp, color = AppColors.SubText, modifier = Modifier.padding(top = 2.dp))
                    if (item.warnings.isNotEmpty()) {
                        FlowRow(modifier = Modifier.padding(top = 7.dp), horizontalArrangement = Arrangement.spacedBy(5.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            item.warnings.forEach { WarningBadge(warning = it) }
                        }
                    }
                }
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = AppColors.SubText.copy(alpha = 0.5f), modifier = Modifier.size(16.dp).align(Alignment.CenterVertically))
            }
        }
    }
}

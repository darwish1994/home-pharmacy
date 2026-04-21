package com.dwa.fridgepharmacy.feature.medication.ui.detail

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dwa.fridgepharmacy.feature.medication.domain.MedicationWarning
import com.dwa.fridgepharmacy.feature.medication.domain.StorageCondition
import com.dwa.fridgepharmacy.core.ui.theme.AppColors
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun MedicationDetailScreen(
    medicationId: Long,
    onNavigateBack: () -> Unit,
    onEditClick: (Long) -> Unit,
    viewModel: MedicationDetailViewModel = koinViewModel { parametersOf(medicationId) }
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.isDeleted) {
        if (state.isDeleted) onNavigateBack()
    }

    val med = state.medication ?: return

    val topWarning = state.warnings.firstOrNull()
    val heroBg = when (topWarning) {
        is MedicationWarning.Expired -> AppColors.RedLight
        is MedicationWarning.ExpiresSoon, is MedicationWarning.ShouldNotBeRefrigerated,
        is MedicationWarning.TemperatureOutOfRange -> AppColors.AmberLight
        is MedicationWarning.InfoKeepRefrigerated -> AppColors.BlueLight
        null -> AppColors.GreenLight
    }
    val heroColor = when (topWarning) {
        is MedicationWarning.Expired -> AppColors.Red
        is MedicationWarning.ExpiresSoon, is MedicationWarning.ShouldNotBeRefrigerated,
        is MedicationWarning.TemperatureOutOfRange -> AppColors.Amber
        is MedicationWarning.InfoKeepRefrigerated -> AppColors.Blue
        null -> AppColors.Green
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background)
            .verticalScroll(rememberScrollState())
    ) {
        // Nav bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 60.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onNavigateBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = AppColors.Green, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(4.dp))
                Text("Back", color = AppColors.Green, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            }
            TextButton(onClick = { onEditClick(medicationId) }) {
                Text("Edit", color = AppColors.Green, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            }
        }

        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 20.dp)) {
            // Title card
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = AppColors.Card,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    // Hero icon
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(110.dp)
                                .clip(RoundedCornerShape(28.dp))
                                .background(heroBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = med.name.first().uppercase(),
                                fontSize = 40.sp,
                                fontWeight = FontWeight.Bold,
                                color = heroColor
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    // Name + quantity
                    Text(
                        text = med.name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.Text,
                        letterSpacing = (-0.4).sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = "${med.quantity}",
                        fontSize = 14.sp,
                        color = AppColors.SubText,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(top = 2.dp)
                    )

                    Spacer(Modifier.height(14.dp))

                    // Info grid
                    val condLabel = when (med.storageCondition) {
                        StorageCondition.REFRIGERATE -> "Refrigerate"
                        StorageCondition.ROOM_TEMPERATURE -> "Room Temperature"
                        StorageCondition.AVOID_FRIDGE -> "Avoid Fridge"
                        StorageCondition.SPECIFIC_RANGE -> "Specific Range"
                    }
                    val tempRange = when (med.storageCondition) {
                        StorageCondition.SPECIFIC_RANGE -> "${med.minTemp?.toInt() ?: 0}-${med.maxTemp?.toInt() ?: 0}°C"
                        StorageCondition.REFRIGERATE -> "2-8°C"
                        else -> "N/A"
                    }
                    val infoItems = listOf(
                        "Expiry Date" to med.expiryDate.toString(),
                        "Storage" to condLabel,
                        "Quantity" to "${med.quantity}",
                        "Temp Range" to tempRange
                    )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            InfoTile(infoItems[0].first, infoItems[0].second)
                            InfoTile(infoItems[2].first, infoItems[2].second)
                        }
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            InfoTile(infoItems[1].first, infoItems[1].second)
                            InfoTile(infoItems[3].first, infoItems[3].second)
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // Warnings / Alerts
            if (state.warnings.isNotEmpty()) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = AppColors.Card,
                    shadowElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text(
                            text = "ALERTS",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AppColors.SubText,
                            letterSpacing = 0.5.sp,
                            modifier = Modifier.padding(start = 16.dp, top = 12.dp, bottom = 8.dp)
                        )
                        state.warnings.forEachIndexed { index, warning ->
                            if (index > 0) HorizontalDivider(color = AppColors.Separator)
                            val (warnText, warnColor, warnBg) = when (warning) {
                                is MedicationWarning.Expired -> Triple("EXPIRED", AppColors.Red, AppColors.RedLight)
                                is MedicationWarning.ExpiresSoon -> Triple("Expires in ${warning.daysLeft}d", AppColors.Amber, AppColors.AmberLight)
                                is MedicationWarning.InfoKeepRefrigerated -> Triple("Keep refrigerated", AppColors.Blue, AppColors.BlueLight)
                                is MedicationWarning.ShouldNotBeRefrigerated -> Triple("Should not be refrigerated", AppColors.Amber, AppColors.AmberLight)
                                is MedicationWarning.TemperatureOutOfRange ->
                                    Triple("Temp out of range (${warning.minRequired.toInt()}-${warning.maxRequired.toInt()}°C required)", AppColors.Amber, AppColors.AmberLight)
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(warnBg)
                                    .padding(horizontal = 16.dp, vertical = 11.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = when (warning) {
                                        is MedicationWarning.Expired -> Icons.Default.Warning
                                        is MedicationWarning.InfoKeepRefrigerated -> Icons.Default.Info
                                        else -> Icons.Default.Warning
                                    },
                                    contentDescription = null,
                                    tint = warnColor,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(Modifier.width(12.dp))
                                Text(warnText, fontSize = 14.sp, color = warnColor, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))
            }

            // Delete button
            Button(
                onClick = { viewModel.delete() },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppColors.RedLight,
                    contentColor = AppColors.Red
                )
            ) {
                Icon(Icons.Default.Delete, null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(8.dp))
                Text("Remove Medication", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            }
        }
    }
}

@Composable
private fun InfoTile(label: String, value: String) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = AppColors.Background
    ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
            Text(
                text = label.uppercase(),
                fontSize = 11.sp,
                color = AppColors.SubText,
                letterSpacing = 0.4.sp
            )
            Spacer(Modifier.height(3.dp))
            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = AppColors.Text
            )
        }
    }
}

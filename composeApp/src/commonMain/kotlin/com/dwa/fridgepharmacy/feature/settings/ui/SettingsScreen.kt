package com.dwa.fridgepharmacy.feature.settings.ui

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dwa.fridgepharmacy.core.ui.theme.AppColors

@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit
) {
    var fridgeTemp by remember { mutableFloatStateOf(4f) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background)
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
            Text("Settings", fontSize = 17.sp, fontWeight = FontWeight.SemiBold, color = AppColors.Text)
            Spacer(Modifier.width(60.dp))
        }

        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp)) {
            // Fridge section label
            Text(
                text = "FRIDGE",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = AppColors.SubText,
                letterSpacing = 0.5.sp,
                modifier = Modifier.padding(start = 4.dp, bottom = 10.dp)
            )

            // Fridge temperature card
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = AppColors.Card,
                shadowElevation = 1.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(AppColors.BlueLight),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("\uD83C\uDF21\uFE0F", fontSize = 18.sp)
                                }
                                Spacer(Modifier.width(10.dp))
                                Column {
                                    Text("Fridge Temperature", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = AppColors.Text)
                                    Text("Used for storage warnings", fontSize = 13.sp, color = AppColors.SubText)
                                }
                            }
                            Text(
                                text = "${fridgeTemp.toInt()}°C",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = AppColors.Green
                            )
                        }

                        Spacer(Modifier.height(14.dp))

                        Slider(
                            value = fridgeTemp,
                            onValueChange = { fridgeTemp = it },
                            valueRange = -5f..15f,
                            steps = 19,
                            colors = SliderDefaults.colors(
                                thumbColor = AppColors.Green,
                                activeTrackColor = AppColors.Green
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("-5°C", fontSize = 12.sp, color = AppColors.SubText)
                            Text("15°C", fontSize = 12.sp, color = AppColors.SubText)
                        }
                    }

                    HorizontalDivider(color = AppColors.Separator)

                    Row(
                        modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Info, null, tint = AppColors.Blue, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Ideal fridge temp for medication storage is 2-8°C",
                            fontSize = 13.sp,
                            color = AppColors.SubText
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // About section
            Text(
                text = "ABOUT",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = AppColors.SubText,
                letterSpacing = 0.5.sp,
                modifier = Modifier.padding(start = 4.dp, bottom = 10.dp)
            )

            Surface(
                shape = RoundedCornerShape(18.dp),
                color = AppColors.Card,
                shadowElevation = 1.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    listOf(
                        "Version" to "1.0.0",
                        "Platform" to "iOS & Android",
                        "Build" to "2026.1"
                    ).forEachIndexed { index, (key, value) ->
                        if (index > 0) HorizontalDivider(color = AppColors.Separator)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 18.dp, vertical = 14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(key, fontSize = 16.sp, color = AppColors.Text)
                            Text(value, fontSize = 16.sp, color = AppColors.SubText)
                        }
                    }
                }
            }
        }
    }
}

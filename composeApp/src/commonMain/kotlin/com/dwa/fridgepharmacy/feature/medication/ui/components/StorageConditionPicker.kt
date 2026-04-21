package com.dwa.fridgepharmacy.feature.medication.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dwa.fridgepharmacy.core.ui.theme.AppColors
import com.dwa.fridgepharmacy.feature.medication.domain.StorageCondition

private fun StorageCondition.displayName(): String = when (this) {
    StorageCondition.ROOM_TEMPERATURE -> "Room Temperature"
    StorageCondition.REFRIGERATE -> "Refrigerate (2-8°C)"
    StorageCondition.AVOID_FRIDGE -> "Avoid Fridge"
    StorageCondition.SPECIFIC_RANGE -> "Specific Range"
}

@Composable
fun StorageConditionPicker(
    selected: StorageCondition,
    onSelected: (StorageCondition) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("STORAGE CONDITION", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = AppColors.SubText, letterSpacing = 0.5.sp, modifier = Modifier.padding(bottom = 2.dp))
        StorageCondition.entries.forEach { condition ->
            val isSelected = condition == selected
            Surface(
                modifier = Modifier.fillMaxWidth().clickable { onSelected(condition) },
                shape = RoundedCornerShape(12.dp),
                color = if (isSelected) AppColors.GreenLight else AppColors.Card,
                border = BorderStroke(1.5.dp, if (isSelected) AppColors.Green else AppColors.Separator)
            ) {
                Row(modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(20.dp).border(2.dp, if (isSelected) AppColors.Green else AppColors.Separator, CircleShape), contentAlignment = Alignment.Center) {
                        if (isSelected) Surface(modifier = Modifier.size(10.dp), shape = CircleShape, color = AppColors.Green) {}
                    }
                    Spacer(Modifier.width(12.dp))
                    Text(condition.displayName(), fontSize = 15.sp, color = AppColors.Text, fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal)
                }
            }
        }
    }
}

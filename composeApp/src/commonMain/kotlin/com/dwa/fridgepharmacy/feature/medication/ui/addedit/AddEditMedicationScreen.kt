package com.dwa.fridgepharmacy.feature.medication.ui.addedit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dwa.fridgepharmacy.feature.medication.domain.StorageCondition
import com.dwa.fridgepharmacy.core.ui.components.DatePickerField
import com.dwa.fridgepharmacy.feature.medication.ui.components.StorageConditionPicker
import com.dwa.fridgepharmacy.core.ui.theme.AppColors
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun AddEditMedicationScreen(
    medicationId: Long?,
    onNavigateBack: () -> Unit,
    viewModel: AddEditMedicationViewModel = koinViewModel { parametersOf(medicationId) }
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.isSaved) {
        if (state.isSaved) onNavigateBack()
    }

    val fieldColors = OutlinedTextFieldDefaults.colors(
        unfocusedBorderColor = AppColors.Separator,
        focusedBorderColor = AppColors.Green,
        unfocusedContainerColor = AppColors.Card,
        focusedContainerColor = AppColors.Card,
        errorBorderColor = AppColors.Red,
    )

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
                Text("Cancel", color = AppColors.Green, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            }
            Text(
                if (state.isEditing) "Edit Medication" else "Add Medication",
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                color = AppColors.Text
            )
            TextButton(onClick = viewModel::save) {
                Text("Save", color = AppColors.Green, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Name field
            FieldLabel("MEDICATION NAME")
            OutlinedTextField(
                value = state.name,
                onValueChange = viewModel::updateName,
                placeholder = { Text("e.g. Amoxicillin 500mg", color = AppColors.SubText) },
                isError = state.nameError != null,
                colors = fieldColors,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().height(52.dp),
                singleLine = true
            )
            state.nameError?.let {
                Text(it, fontSize = 12.sp, color = AppColors.Red, modifier = Modifier.padding(top = 4.dp))
            }

            Spacer(Modifier.height(16.dp))

            // Quantity
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    FieldLabel("QUANTITY")
                    OutlinedTextField(
                        value = state.quantity,
                        onValueChange = viewModel::updateQuantity,
                        placeholder = { Text("0") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = state.quantityError != null,
                        colors = fieldColors,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        singleLine = true
                    )
                    state.quantityError?.let {
                        Text(it, fontSize = 12.sp, color = AppColors.Red, modifier = Modifier.padding(top = 4.dp))
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Expiry date
            FieldLabel("EXPIRY DATE")
            DatePickerField(
                date = state.expiryDate,
                onDateSelected = viewModel::updateExpiryDate
            )

            Spacer(Modifier.height(16.dp))

            // Storage condition
            StorageConditionPicker(
                selected = state.storageCondition,
                onSelected = viewModel::updateStorageCondition
            )

            // Temp range for SPECIFIC_RANGE
            if (state.storageCondition == StorageCondition.SPECIFIC_RANGE) {
                Spacer(Modifier.height(16.dp))
                FieldLabel("TEMPERATURE RANGE (°C)")
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Min °C", fontSize = 11.sp, color = AppColors.SubText, modifier = Modifier.padding(bottom = 4.dp))
                        OutlinedTextField(
                            value = state.minTemp,
                            onValueChange = viewModel::updateMinTemp,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            colors = fieldColors,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().height(52.dp),
                            singleLine = true
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Max °C", fontSize = 11.sp, color = AppColors.SubText, modifier = Modifier.padding(bottom = 4.dp))
                        OutlinedTextField(
                            value = state.maxTemp,
                            onValueChange = viewModel::updateMaxTemp,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            colors = fieldColors,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().height(52.dp),
                            singleLine = true
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // Save button
            Button(
                onClick = viewModel::save,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppColors.Green,
                    contentColor = Color.White
                )
            ) {
                Text(
                    if (state.isEditing) "Save Changes" else "Add to Pharmacy",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            }
        }
    }
}

@Composable
private fun FieldLabel(text: String) {
    Text(
        text = text,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        color = AppColors.SubText,
        letterSpacing = 0.5.sp,
        modifier = Modifier.padding(bottom = 6.dp)
    )
}

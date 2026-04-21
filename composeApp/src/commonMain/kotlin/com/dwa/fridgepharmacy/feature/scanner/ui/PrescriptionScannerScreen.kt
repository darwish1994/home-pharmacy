package com.dwa.fridgepharmacy.feature.scanner.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.ColumnScope
import com.dwa.fridgepharmacy.core.ui.theme.AppColors
import com.dwa.fridgepharmacy.feature.scanner.data.rememberImagePickerLauncher
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PrescriptionScannerScreen(
    onNavigateBack: () -> Unit,
    viewModel: PrescriptionScannerViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    val imagePicker = rememberImagePickerLauncher { imageBytes ->
        viewModel.processImage(imageBytes)
    }

    LaunchedEffect(state.isSaved) {
        if (state.isSaved) onNavigateBack()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background)
    ) {
        NavBar(onNavigateBack)

        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 20.dp)) {
            when {
                state.isProcessing -> ScanningState()
                state.parsedMedications.isNotEmpty() -> ResultsState(state, viewModel)
                else -> PickState(
                    error = state.error,
                    onTakePhoto = imagePicker.launchCamera,
                    onFromGallery = imagePicker.launchGallery
                )
            }
        }
    }
}

@Composable
private fun NavBar(onNavigateBack: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, top = 60.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(onClick = onNavigateBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = AppColors.Green, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(4.dp))
            Text("Back", color = AppColors.Green, fontSize = 16.sp, fontWeight = FontWeight.Medium)
        }
        Text("Scan Prescription", fontSize = 17.sp, fontWeight = FontWeight.SemiBold, color = AppColors.Text)
        Spacer(Modifier.width(60.dp))
    }
}

@Composable
private fun PickState(
    error: String?,
    onTakePhoto: () -> Unit,
    onFromGallery: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0x0A000000))
            .border(2.dp, AppColors.Separator, RoundedCornerShape(20.dp)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(shape = CircleShape, color = AppColors.GreenLight, modifier = Modifier.size(56.dp)) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Text("\uD83D\uDCF7", fontSize = 24.sp)
                }
            }
            Spacer(Modifier.height(10.dp))
            Text("Take or upload a photo of\nyour prescription", fontSize = 15.sp, color = AppColors.SubText, textAlign = TextAlign.Center)
        }
    }

    Spacer(Modifier.height(16.dp))

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Button(
            onClick = onTakePhoto,
            modifier = Modifier.weight(1f).height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AppColors.Green, contentColor = Color.White)
        ) {
            Text("Take Photo", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
        }
        OutlinedButton(
            onClick = onFromGallery,
            modifier = Modifier.weight(1f).height(52.dp),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text("From Gallery", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
        }
    }

    error?.let {
        Spacer(Modifier.height(16.dp))
        Text(it, color = AppColors.Red, fontSize = 14.sp)
    }
}

@Composable
private fun ScanningState() {
    Box(modifier = Modifier.fillMaxWidth().height(300.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = AppColors.Green)
            Spacer(Modifier.height(16.dp))
            Text("Extracting medications\u2026", fontSize = 16.sp, color = AppColors.SubText)
        }
    }
}

@Composable
private fun ColumnScope.ResultsState(state: ScannerUiState, viewModel: PrescriptionScannerViewModel) {
    Surface(shape = RoundedCornerShape(14.dp), color = AppColors.GreenLight, modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Check, null, tint = AppColors.Green, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(10.dp))
            Text("Found ${state.parsedMedications.size} medications. Review and add.", fontSize = 14.sp, color = AppColors.Green, fontWeight = FontWeight.Medium)
        }
    }

    Spacer(Modifier.height(16.dp))

    LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        itemsIndexed(state.parsedMedications) { index, med ->
            Surface(shape = RoundedCornerShape(14.dp), color = AppColors.Card, shadowElevation = 1.dp, modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(24.dp).clip(RoundedCornerShape(6.dp)).background(AppColors.Green).clickable { viewModel.removeMedication(index) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(13.dp))
                    }
                    Spacer(Modifier.width(12.dp))
                    Text(med.name, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = AppColors.Text, modifier = Modifier.weight(1f))
                    OutlinedTextField(
                        value = med.quantity.toString(),
                        onValueChange = { it.toIntOrNull()?.let { qty -> viewModel.updateMedicationQuantity(index, qty) } },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = AppColors.Separator, focusedBorderColor = AppColors.Green, unfocusedContainerColor = AppColors.Background, focusedContainerColor = AppColors.Background),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.width(60.dp).height(40.dp),
                        singleLine = true
                    )
                    Spacer(Modifier.width(6.dp))
                    Text("pcs", fontSize = 13.sp, color = AppColors.SubText)
                }
            }
        }
    }

    Spacer(Modifier.height(16.dp))

    Button(
        onClick = viewModel::addToPharmacy,
        modifier = Modifier.fillMaxWidth().height(52.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = AppColors.Green, contentColor = Color.White)
    ) {
        Text("Add ${state.parsedMedications.size} to Pharmacy", fontWeight = FontWeight.Bold, fontSize = 17.sp)
    }
}

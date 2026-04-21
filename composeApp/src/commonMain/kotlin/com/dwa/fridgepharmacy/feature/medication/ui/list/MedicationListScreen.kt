package com.dwa.fridgepharmacy.feature.medication.ui.list

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dwa.fridgepharmacy.core.ui.theme.AppColors
import com.dwa.fridgepharmacy.feature.medication.ui.components.MedicationCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicationListScreen(
    uiState: MedicationListUiState,
    onEvent: (MedicationListUiEvent) -> Unit,
    onAddClick: () -> Unit,
    onMedicationClick: (Long) -> Unit,
    onScanClick: () -> Unit,
    onSettingsClick: () -> Unit,
) {
    Scaffold(
        containerColor = AppColors.Background,
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick, modifier = Modifier.padding(bottom = 16.dp).size(56.dp), shape = CircleShape, containerColor = AppColors.Green, contentColor = Color.White) {
                Icon(Icons.Default.Add, contentDescription = "Add medication")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            ListHeader(uiState.medications.size, uiState.expiredCount, uiState.warningCount, uiState.searchQuery, { onEvent(MedicationListUiEvent.SearchQueryChanged(it)) }, onScanClick, onSettingsClick)
            MedicationList(uiState.filteredMedications, uiState.medications.isEmpty(), { onMedicationClick(it.medication.id) }, { onEvent(MedicationListUiEvent.DeleteMedication(it.medication.id)) })
        }
    }
}

@Composable
private fun ListHeader(medicationCount: Int, expiredCount: Int, warningCount: Int, searchQuery: String, onSearchChange: (String) -> Unit, onScanClick: () -> Unit, onSettingsClick: () -> Unit) {
    Column(modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text("My Pharmacy", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = AppColors.Text, letterSpacing = (-0.5).sp)
                Text("$medicationCount medications", fontSize = 13.sp, color = AppColors.SubText, modifier = Modifier.padding(top = 1.dp))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                CircleIconButton(onClick = onScanClick, color = AppColors.GreenLight) { Text("Scan", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = AppColors.Green) }
                CircleIconButton(onClick = onSettingsClick, color = Color(0x0D000000)) { Icon(Icons.Default.Settings, contentDescription = "Settings", tint = AppColors.SubText, modifier = Modifier.size(18.dp)) }
            }
        }
        SummaryChips(expiredCount, warningCount)
        TextField(searchQuery, onSearchChange, placeholder = { Text("Search medications\u2026", fontSize = 15.sp) }, singleLine = true, shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(unfocusedContainerColor = AppColors.SearchBg, focusedContainerColor = AppColors.SearchBg, unfocusedIndicatorColor = Color.Transparent, focusedIndicatorColor = Color.Transparent),
            modifier = Modifier.fillMaxWidth().padding(top = 14.dp))
    }
}

@Composable
private fun CircleIconButton(onClick: () -> Unit, color: Color, content: @Composable () -> Unit) {
    Surface(onClick = onClick, shape = CircleShape, color = color, modifier = Modifier.size(40.dp)) { Box(contentAlignment = Alignment.Center) { content() } }
}

@Composable
private fun SummaryChips(expiredCount: Int, warningCount: Int) {
    if (expiredCount == 0 && warningCount == 0) return
    Row(modifier = Modifier.padding(top = 12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        if (expiredCount > 0) StatusChip("$expiredCount expired", AppColors.Red, AppColors.RedLight)
        if (warningCount > 0) StatusChip("$warningCount expiring soon", AppColors.Amber, AppColors.AmberLight)
    }
}

@Composable
private fun StatusChip(label: String, color: Color, background: Color) {
    Surface(shape = RoundedCornerShape(20.dp), color = background) {
        Row(modifier = Modifier.padding(horizontal = 9.dp, vertical = 3.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Warning, null, tint = color, modifier = Modifier.size(12.dp))
            Spacer(Modifier.width(3.dp))
            Text(label, color = color, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MedicationList(items: List<MedicationWithWarnings>, isEmpty: Boolean, onItemClick: (MedicationWithWarnings) -> Unit, onItemDelete: (MedicationWithWarnings) -> Unit) {
    when {
        isEmpty -> EmptyState("No medications yet", "Tap + to add or Scan a prescription")
        items.isEmpty() -> EmptyState("No medications found")
        else -> LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 80.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(items = items, key = { it.medication.id }) { item ->
                DismissableMedicationCard(item, { onItemClick(item) }, { onItemDelete(item) })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DismissableMedicationCard(item: MedicationWithWarnings, onClick: () -> Unit, onDelete: () -> Unit) {
    val dismissState = rememberSwipeToDismissBoxState()
    if (dismissState.currentValue == SwipeToDismissBoxValue.EndToStart) { LaunchedEffect(item.medication.id) { onDelete() } }
    SwipeToDismissBox(state = dismissState, backgroundContent = {
        val color by animateColorAsState(if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart) AppColors.Red else Color.Transparent)
        Box(modifier = Modifier.fillMaxSize().background(color, RoundedCornerShape(16.dp)).padding(horizontal = 20.dp), contentAlignment = Alignment.CenterEnd) {
            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.White)
        }
    }, enableDismissFromStartToEnd = false) { MedicationCard(item = item, onClick = onClick) }
}

@Composable
private fun EmptyState(title: String, subtitle: String? = null) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(title, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = AppColors.SubText)
            if (subtitle != null) Text(subtitle, fontSize = 14.sp, color = AppColors.SubText, modifier = Modifier.padding(top = 8.dp))
        }
    }
}

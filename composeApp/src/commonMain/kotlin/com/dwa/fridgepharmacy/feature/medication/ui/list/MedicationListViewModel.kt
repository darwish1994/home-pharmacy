package com.dwa.fridgepharmacy.feature.medication.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dwa.fridgepharmacy.feature.medication.data.MedicationRepository
import com.dwa.fridgepharmacy.feature.medication.domain.Medication
import com.dwa.fridgepharmacy.feature.medication.domain.MedicationWarning
import com.dwa.fridgepharmacy.feature.medication.domain.WarningCalculator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class MedicationWithWarnings(
    val medication: Medication,
    val warnings: List<MedicationWarning>
)

data class MedicationListUiState(
    val medications: List<MedicationWithWarnings> = emptyList(),
    val filteredMedications: List<MedicationWithWarnings> = emptyList(),
    val searchQuery: String = "",
    val expiredCount: Int = 0,
    val warningCount: Int = 0
)

sealed interface MedicationListUiEvent {
    data class SearchQueryChanged(val query: String) : MedicationListUiEvent
    data class DeleteMedication(val id: Long) : MedicationListUiEvent
}

class MedicationListViewModel(
    private val repository: MedicationRepository
) : ViewModel() {

    private val searchQuery = MutableStateFlow("")

    private val medicationsWithWarnings = repository.getAll()
        .map { list ->
            list.map { med ->
                MedicationWithWarnings(medication = med, warnings = WarningCalculator.getWarnings(med))
            }
        }

    val uiState: StateFlow<MedicationListUiState> = combine(
        medicationsWithWarnings, searchQuery
    ) { meds, query ->
        val filtered = if (query.isBlank()) meds
        else meds.filter { it.medication.name.contains(query, ignoreCase = true) }

        MedicationListUiState(
            medications = meds,
            filteredMedications = filtered,
            searchQuery = query,
            expiredCount = meds.count { it.warnings.any { w -> w is MedicationWarning.Expired } },
            warningCount = meds.count { it.warnings.any { w -> w is MedicationWarning.ExpiresSoon } }
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MedicationListUiState())

    fun onEvent(event: MedicationListUiEvent) {
        when (event) {
            is MedicationListUiEvent.SearchQueryChanged -> searchQuery.value = event.query
            is MedicationListUiEvent.DeleteMedication -> viewModelScope.launch { repository.delete(event.id) }
        }
    }
}

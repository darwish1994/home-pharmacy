package com.dwa.fridgepharmacy.feature.medication.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dwa.fridgepharmacy.feature.medication.data.MedicationRepository
import com.dwa.fridgepharmacy.feature.medication.domain.WarningCalculator
import com.dwa.fridgepharmacy.feature.medication.domain.Medication
import com.dwa.fridgepharmacy.feature.medication.domain.MedicationWarning
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DetailUiState(
    val medication: Medication? = null,
    val warnings: List<MedicationWarning> = emptyList(),
    val isDeleted: Boolean = false
)

class MedicationDetailViewModel(
    private val repository: MedicationRepository,
    private val medicationId: Long
) : ViewModel() {

    private val _state = MutableStateFlow(DetailUiState())
    val state: StateFlow<DetailUiState> = _state.asStateFlow()

    init {
        loadMedication()
    }

    private fun loadMedication() {
        viewModelScope.launch {
            val med = repository.getById(medicationId)
            if (med != null) {
                _state.value = DetailUiState(
                    medication = med,
                    warnings = WarningCalculator.getWarnings(med)
                )
            }
        }
    }

    fun delete() {
        viewModelScope.launch {
            repository.delete(medicationId)
            _state.value = _state.value.copy(isDeleted = true)
        }
    }
}

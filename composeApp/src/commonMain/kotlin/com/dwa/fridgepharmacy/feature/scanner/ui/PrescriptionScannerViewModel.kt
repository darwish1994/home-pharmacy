package com.dwa.fridgepharmacy.feature.scanner.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dwa.fridgepharmacy.feature.medication.data.MedicationRepository
import com.dwa.fridgepharmacy.feature.medication.domain.Medication
import com.dwa.fridgepharmacy.feature.medication.domain.StorageCondition
import com.dwa.fridgepharmacy.feature.scanner.data.OcrParser
import com.dwa.fridgepharmacy.feature.scanner.data.ParsedMedication
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.dwa.fridgepharmacy.core.util.today
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.plus

data class ScannerUiState(
    val parsedMedications: List<ParsedMedication> = emptyList(),
    val isProcessing: Boolean = false,
    val error: String? = null,
    val isSaved: Boolean = false
)

class PrescriptionScannerViewModel(
    private val repository: MedicationRepository,
    private val ocrParser: OcrParser
) : ViewModel() {

    private val _state = MutableStateFlow(ScannerUiState())
    val state: StateFlow<ScannerUiState> = _state.asStateFlow()

    fun processImage(imageBytes: ByteArray) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isProcessing = true, error = null)
            try {
                val results = ocrParser.parseImage(imageBytes)
                if (results.isEmpty()) {
                    _state.value = _state.value.copy(
                        isProcessing = false,
                        error = "No medications found in the image. You can add them manually."
                    )
                } else {
                    _state.value = _state.value.copy(
                        parsedMedications = results,
                        isProcessing = false
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isProcessing = false,
                    error = "OCR failed: ${e.message}. Please try again or add manually."
                )
            }
        }
    }

    fun updateMedicationName(index: Int, name: String) {
        val list = _state.value.parsedMedications.toMutableList()
        if (index in list.indices) {
            list[index] = list[index].copy(name = name)
            _state.value = _state.value.copy(parsedMedications = list)
        }
    }

    fun updateMedicationQuantity(index: Int, quantity: Int) {
        val list = _state.value.parsedMedications.toMutableList()
        if (index in list.indices) {
            list[index] = list[index].copy(quantity = quantity)
            _state.value = _state.value.copy(parsedMedications = list)
        }
    }

    fun removeMedication(index: Int) {
        val list = _state.value.parsedMedications.toMutableList()
        if (index in list.indices) {
            list.removeAt(index)
            _state.value = _state.value.copy(parsedMedications = list)
        }
    }

    fun addToPharmacy() {
        viewModelScope.launch {
            val today = today()
            val defaultExpiry = today.plus(365, DateTimeUnit.DAY)

            for (parsed in _state.value.parsedMedications) {
                if (parsed.name.isNotBlank()) {
                    repository.insert(
                        Medication(
                            name = parsed.name,
                            quantity = parsed.quantity,
                            expiryDate = defaultExpiry,
                            storageCondition = StorageCondition.REFRIGERATE
                        )
                    )
                }
            }
            _state.value = _state.value.copy(isSaved = true)
        }
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }
}

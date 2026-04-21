package com.dwa.fridgepharmacy.feature.medication.ui.addedit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dwa.fridgepharmacy.feature.medication.data.MedicationRepository
import com.dwa.fridgepharmacy.feature.medication.domain.Medication
import com.dwa.fridgepharmacy.feature.medication.domain.StorageCondition
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.dwa.fridgepharmacy.core.util.today
import kotlinx.datetime.LocalDate

data class AddEditUiState(
    val name: String = "",
    val quantity: String = "1",
    val expiryDate: LocalDate = today(),
    val storageCondition: StorageCondition = StorageCondition.REFRIGERATE,
    val minTemp: String = "",
    val maxTemp: String = "",
    val isEditing: Boolean = false,
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val nameError: String? = null,
    val quantityError: String? = null
)

class AddEditMedicationViewModel(
    private val repository: MedicationRepository,
    private val medicationId: Long?
) : ViewModel() {

    private val _state = MutableStateFlow(AddEditUiState())
    val state: StateFlow<AddEditUiState> = _state.asStateFlow()

    init {
        if (medicationId != null) {
            loadMedication(medicationId)
        }
    }

    private fun loadMedication(id: Long) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val medication = repository.getById(id)
            if (medication != null) {
                _state.value = _state.value.copy(
                    name = medication.name,
                    quantity = medication.quantity.toString(),
                    expiryDate = medication.expiryDate,
                    storageCondition = medication.storageCondition,
                    minTemp = medication.minTemp?.toString() ?: "",
                    maxTemp = medication.maxTemp?.toString() ?: "",
                    isEditing = true,
                    isLoading = false
                )
            } else {
                _state.value = _state.value.copy(isLoading = false)
            }
        }
    }

    fun updateName(name: String) {
        _state.value = _state.value.copy(name = name, nameError = null)
    }

    fun updateQuantity(quantity: String) {
        _state.value = _state.value.copy(quantity = quantity, quantityError = null)
    }

    fun updateExpiryDate(date: LocalDate) {
        _state.value = _state.value.copy(expiryDate = date)
    }

    fun updateStorageCondition(condition: StorageCondition) {
        _state.value = _state.value.copy(storageCondition = condition)
    }

    fun updateMinTemp(temp: String) {
        _state.value = _state.value.copy(minTemp = temp)
    }

    fun updateMaxTemp(temp: String) {
        _state.value = _state.value.copy(maxTemp = temp)
    }

    fun save() {
        val currentState = _state.value
        var hasError = false

        if (currentState.name.isBlank()) {
            _state.value = _state.value.copy(nameError = "Name is required")
            hasError = true
        }

        val qty = currentState.quantity.toIntOrNull()
        if (qty == null || qty <= 0) {
            _state.value = _state.value.copy(quantityError = "Quantity must be greater than 0")
            hasError = true
        }

        if (hasError) return

        viewModelScope.launch {
            val medication = Medication(
                id = medicationId ?: 0,
                name = currentState.name.trim(),
                quantity = qty!!,
                expiryDate = currentState.expiryDate,
                storageCondition = currentState.storageCondition,
                minTemp = if (currentState.storageCondition == StorageCondition.SPECIFIC_RANGE)
                    currentState.minTemp.toDoubleOrNull() else null,
                maxTemp = if (currentState.storageCondition == StorageCondition.SPECIFIC_RANGE)
                    currentState.maxTemp.toDoubleOrNull() else null
            )

            if (currentState.isEditing) {
                repository.update(medication)
            } else {
                repository.insert(medication)
            }
            _state.value = _state.value.copy(isSaved = true)
        }
    }
}

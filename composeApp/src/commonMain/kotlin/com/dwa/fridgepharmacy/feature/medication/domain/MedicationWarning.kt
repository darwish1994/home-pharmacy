package com.dwa.fridgepharmacy.feature.medication.domain

sealed class MedicationWarning {
    data object Expired : MedicationWarning()
    data class ExpiresSoon(val daysLeft: Int) : MedicationWarning()
    data object InfoKeepRefrigerated : MedicationWarning()
    data object ShouldNotBeRefrigerated : MedicationWarning()
    data class TemperatureOutOfRange(
        val currentTemp: Double,
        val minRequired: Double,
        val maxRequired: Double
    ) : MedicationWarning()
}

package com.dwa.fridgepharmacy.feature.medication.domain

import com.dwa.fridgepharmacy.core.util.today
import kotlinx.datetime.LocalDate
import kotlinx.datetime.daysUntil

object WarningCalculator {
    private const val DEFAULT_FRIDGE_TEMP = 4.0

    fun getWarnings(
        medication: Medication,
        today: LocalDate = today(),
        fridgeTemp: Double = DEFAULT_FRIDGE_TEMP
    ): List<MedicationWarning> {
        val warnings = mutableListOf<MedicationWarning>()
        val daysUntilExpiry = today.daysUntil(medication.expiryDate)

        when {
            daysUntilExpiry < 0 -> warnings.add(MedicationWarning.Expired)
            daysUntilExpiry <= 7 -> warnings.add(MedicationWarning.ExpiresSoon(daysUntilExpiry))
        }

        when (medication.storageCondition) {
            StorageCondition.REFRIGERATE ->
                warnings.add(MedicationWarning.InfoKeepRefrigerated)
            StorageCondition.AVOID_FRIDGE ->
                warnings.add(MedicationWarning.ShouldNotBeRefrigerated)
            StorageCondition.SPECIFIC_RANGE -> {
                val min = medication.minTemp
                val max = medication.maxTemp
                if (min != null && max != null && (fridgeTemp < min || fridgeTemp > max)) {
                    warnings.add(MedicationWarning.TemperatureOutOfRange(fridgeTemp, min, max))
                }
            }
            StorageCondition.ROOM_TEMPERATURE -> { /* no storage warning */ }
        }

        return warnings
    }
}

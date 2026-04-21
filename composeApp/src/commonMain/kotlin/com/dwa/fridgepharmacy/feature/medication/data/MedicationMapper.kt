package com.dwa.fridgepharmacy.feature.medication.data

import com.dwa.fridgepharmacy.db.Medications
import com.dwa.fridgepharmacy.feature.medication.domain.Medication
import com.dwa.fridgepharmacy.feature.medication.domain.StorageCondition
import kotlinx.datetime.LocalDate

fun Medications.toDomain(): Medication {
    return Medication(
        id = id,
        name = name,
        quantity = quantity.toInt(),
        expiryDate = LocalDate.fromEpochDays(expiry_date_epoch_days.toInt()),
        storageCondition = StorageCondition.entries[storage_condition.toInt()],
        minTemp = min_temp,
        maxTemp = max_temp
    )
}

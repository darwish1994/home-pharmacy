package com.dwa.fridgepharmacy.feature.medication.domain

import kotlinx.datetime.LocalDate

data class Medication(
    val id: Long = 0,
    val name: String,
    val quantity: Int,
    val expiryDate: LocalDate,
    val storageCondition: StorageCondition,
    val minTemp: Double? = null,
    val maxTemp: Double? = null
)

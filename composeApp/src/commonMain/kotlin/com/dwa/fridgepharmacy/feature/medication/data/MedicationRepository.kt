package com.dwa.fridgepharmacy.feature.medication.data

import com.dwa.fridgepharmacy.feature.medication.domain.Medication
import kotlinx.coroutines.flow.Flow

interface MedicationRepository {
    fun getAll(): Flow<List<Medication>>
    suspend fun getById(id: Long): Medication?
    suspend fun insert(medication: Medication)
    suspend fun update(medication: Medication)
    suspend fun delete(id: Long)
}

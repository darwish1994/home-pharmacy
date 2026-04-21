package com.dwa.fridgepharmacy.feature.medication.data

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.dwa.fridgepharmacy.db.FridgePharmacyDatabase
import com.dwa.fridgepharmacy.feature.medication.domain.Medication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class MedicationRepositoryImpl(
    private val database: FridgePharmacyDatabase
) : MedicationRepository {

    private val queries = database.medicationsQueries

    override fun getAll(): Flow<List<Medication>> {
        return queries.selectAll()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { list -> list.map { it.toDomain() } }
    }

    override suspend fun getById(id: Long): Medication? {
        return withContext(Dispatchers.IO) {
            queries.selectById(id).executeAsOneOrNull()?.toDomain()
        }
    }

    override suspend fun insert(medication: Medication) {
        withContext(Dispatchers.IO) {
            queries.insert(
                name = medication.name,
                quantity = medication.quantity.toLong(),
                expiry_date_epoch_days = medication.expiryDate.toEpochDays().toLong(),
                storage_condition = medication.storageCondition.ordinal.toLong(),
                min_temp = medication.minTemp,
                max_temp = medication.maxTemp
            )
        }
    }

    override suspend fun update(medication: Medication) {
        withContext(Dispatchers.IO) {
            queries.update(
                name = medication.name,
                quantity = medication.quantity.toLong(),
                expiry_date_epoch_days = medication.expiryDate.toEpochDays().toLong(),
                storage_condition = medication.storageCondition.ordinal.toLong(),
                min_temp = medication.minTemp,
                max_temp = medication.maxTemp,
                id = medication.id
            )
        }
    }

    override suspend fun delete(id: Long) {
        withContext(Dispatchers.IO) {
            queries.deleteById(id)
        }
    }
}

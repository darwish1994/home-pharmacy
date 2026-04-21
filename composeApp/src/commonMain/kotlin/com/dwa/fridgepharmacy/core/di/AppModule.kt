package com.dwa.fridgepharmacy.core.di

import com.dwa.fridgepharmacy.core.data.DatabaseDriverFactory
import com.dwa.fridgepharmacy.db.FridgePharmacyDatabase
import com.dwa.fridgepharmacy.feature.medication.data.MedicationRepository
import com.dwa.fridgepharmacy.feature.medication.data.MedicationRepositoryImpl
import com.dwa.fridgepharmacy.feature.medication.ui.addedit.AddEditMedicationViewModel
import com.dwa.fridgepharmacy.feature.medication.ui.detail.MedicationDetailViewModel
import com.dwa.fridgepharmacy.feature.medication.ui.list.MedicationListViewModel
import com.dwa.fridgepharmacy.feature.scanner.ui.PrescriptionScannerViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val sharedModule = module {
    single {
        val driver = get<DatabaseDriverFactory>().createDriver()
        FridgePharmacyDatabase(driver)
    }
    single<MedicationRepository> { MedicationRepositoryImpl(get()) }
    viewModel { MedicationListViewModel(get()) }
    viewModel { (id: Long?) -> AddEditMedicationViewModel(get(), id) }
    viewModel { (id: Long) -> MedicationDetailViewModel(get(), id) }
    viewModel { PrescriptionScannerViewModel(get(), get()) }
}

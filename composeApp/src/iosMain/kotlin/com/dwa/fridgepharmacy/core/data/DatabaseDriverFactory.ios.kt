package com.dwa.fridgepharmacy.core.data

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.dwa.fridgepharmacy.db.FridgePharmacyDatabase

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(FridgePharmacyDatabase.Schema, "fridgepharmacy.db")
    }
}

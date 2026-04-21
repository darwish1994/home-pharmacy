package com.dwa.fridgepharmacy.core.data

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.dwa.fridgepharmacy.db.FridgePharmacyDatabase

actual class DatabaseDriverFactory(private val context: Context) {
    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(FridgePharmacyDatabase.Schema, context, "fridgepharmacy.db")
    }
}

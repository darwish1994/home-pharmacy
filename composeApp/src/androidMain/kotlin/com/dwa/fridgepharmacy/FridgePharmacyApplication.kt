package com.dwa.fridgepharmacy

import android.app.Application
import com.dwa.fridgepharmacy.core.di.androidModule
import com.dwa.fridgepharmacy.core.di.sharedModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class FridgePharmacyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@FridgePharmacyApplication)
            modules(sharedModule, androidModule)
        }
    }
}

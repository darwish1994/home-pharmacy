package com.dwa.fridgepharmacy.core.di

import com.dwa.fridgepharmacy.core.data.DatabaseDriverFactory
import com.dwa.fridgepharmacy.core.data.preferences.AppPreferences
import com.dwa.fridgepharmacy.feature.scanner.data.AndroidOcrParser
import com.dwa.fridgepharmacy.feature.scanner.data.OcrParser
import org.koin.dsl.module

val androidModule = module {
    single { DatabaseDriverFactory(get()) }
    single { AppPreferences(get()) }
    single<OcrParser> { AndroidOcrParser() }
}

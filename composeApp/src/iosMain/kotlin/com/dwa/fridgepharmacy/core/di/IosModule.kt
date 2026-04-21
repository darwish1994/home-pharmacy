package com.dwa.fridgepharmacy.core.di

import com.dwa.fridgepharmacy.core.data.DatabaseDriverFactory
import com.dwa.fridgepharmacy.core.data.preferences.AppPreferences
import com.dwa.fridgepharmacy.feature.scanner.data.IosOcrParser
import com.dwa.fridgepharmacy.feature.scanner.data.OcrParser
import org.koin.dsl.module

val iosModule = module {
    single { DatabaseDriverFactory() }
    single { AppPreferences() }
    single<OcrParser> { IosOcrParser() }
}

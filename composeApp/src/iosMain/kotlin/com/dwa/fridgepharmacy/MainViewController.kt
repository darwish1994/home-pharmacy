package com.dwa.fridgepharmacy

import androidx.compose.ui.window.ComposeUIViewController
import com.dwa.fridgepharmacy.core.di.iosModule
import com.dwa.fridgepharmacy.core.di.sharedModule
import org.koin.core.context.startKoin

fun initKoin() {
    startKoin {
        modules(sharedModule, iosModule)
    }
}

fun MainViewController() = ComposeUIViewController { App() }

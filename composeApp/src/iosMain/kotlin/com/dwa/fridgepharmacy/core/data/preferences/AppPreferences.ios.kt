package com.dwa.fridgepharmacy.core.data.preferences

import platform.Foundation.NSUserDefaults

actual class AppPreferences {
    private val defaults = NSUserDefaults.standardUserDefaults

    actual fun getBoolean(key: String, default: Boolean): Boolean {
        return if (defaults.objectForKey(key) != null) defaults.boolForKey(key) else default
    }

    actual fun putBoolean(key: String, value: Boolean) {
        defaults.setBool(value, key)
    }
}

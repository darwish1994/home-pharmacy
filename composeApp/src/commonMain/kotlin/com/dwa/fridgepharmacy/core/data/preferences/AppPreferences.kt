package com.dwa.fridgepharmacy.core.data.preferences

expect class AppPreferences {
    fun getBoolean(key: String, default: Boolean): Boolean
    fun putBoolean(key: String, value: Boolean)
}

object PreferenceKeys {
    const val ONBOARDING_COMPLETED = "onboarding_completed"
}

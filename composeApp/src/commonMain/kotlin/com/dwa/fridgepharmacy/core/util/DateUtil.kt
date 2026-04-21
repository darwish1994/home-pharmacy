package com.dwa.fridgepharmacy.core.util

import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

fun today(): LocalDate {
    val instant = kotlinx.datetime.Instant.fromEpochMilliseconds(
        kotlin.time.Clock.System.now().toEpochMilliseconds()
    )
    return instant.toLocalDateTime(TimeZone.currentSystemDefault()).date
}

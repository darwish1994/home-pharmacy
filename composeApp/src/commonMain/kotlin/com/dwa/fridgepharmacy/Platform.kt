package com.dwa.fridgepharmacy

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
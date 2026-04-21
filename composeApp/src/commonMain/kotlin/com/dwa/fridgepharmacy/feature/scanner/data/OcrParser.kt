package com.dwa.fridgepharmacy.feature.scanner.data

interface OcrParser {
    suspend fun parseImage(imageBytes: ByteArray): List<ParsedMedication>
}

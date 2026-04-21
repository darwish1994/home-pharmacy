package com.dwa.fridgepharmacy.feature.scanner.data

import kotlinx.coroutines.delay

class MockOcrParser : OcrParser {
    override suspend fun parseImage(imageBytes: ByteArray): List<ParsedMedication> {
        delay(1500) // simulate OCR processing time
        return listOf(
            ParsedMedication("Ibuprofen 400mg", 2),
            ParsedMedication("Amoxicillin 500mg", 1),
            ParsedMedication("Paracetamol 500mg", 3),
            ParsedMedication("Omeprazole 20mg", 1)
        )
    }
}

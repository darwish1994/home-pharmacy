package com.dwa.fridgepharmacy.feature.scanner.data

import android.graphics.BitmapFactory
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class AndroidOcrParser : OcrParser {

    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    override suspend fun parseImage(imageBytes: ByteArray): List<ParsedMedication> {
        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            ?: return emptyList()
        val inputImage = InputImage.fromBitmap(bitmap, 0)

        val text = suspendCoroutine { continuation ->
            recognizer.process(inputImage)
                .addOnSuccessListener { result ->
                    continuation.resume(result.text)
                }
                .addOnFailureListener { e ->
                    continuation.resumeWithException(e)
                }
        }

        return parseTextToMedications(text)
    }

    private fun parseTextToMedications(text: String): List<ParsedMedication> {
        val lines = text.lines().filter { it.isNotBlank() }
        val medications = mutableListOf<ParsedMedication>()
        val quantityPattern = Regex("""[xX×]\s*(\d+)|(\d+)\s*(tab|cap|pill|ml|pcs|pc|шт)""", RegexOption.IGNORE_CASE)

        for (line in lines) {
            val trimmed = line.trim()
            if (trimmed.length < 3) continue
            // skip lines that are purely numeric or look like headers
            if (trimmed.all { it.isDigit() || it == '.' || it == ',' }) continue

            val quantityMatch = quantityPattern.find(trimmed)
            val quantity = quantityMatch?.let {
                (it.groupValues[1].takeIf { v -> v.isNotEmpty() }
                    ?: it.groupValues[2].takeIf { v -> v.isNotEmpty() })?.toIntOrNull()
            } ?: 1

            val name = if (quantityMatch != null) {
                trimmed.removeRange(quantityMatch.range).trim().trimEnd(',', '.', '-', ' ')
            } else {
                trimmed
            }

            if (name.length >= 3) {
                medications.add(ParsedMedication(name, quantity))
            }
        }

        return medications
    }
}

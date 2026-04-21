package com.dwa.fridgepharmacy.feature.scanner.data

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.create
import platform.UIKit.UIImage
import platform.Vision.VNImageRequestHandler
import platform.Vision.VNRecognizeTextRequest
import platform.Vision.VNRecognizedText
import platform.Vision.VNRecognizedTextObservation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class IosOcrParser : OcrParser {

    @OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
    override suspend fun parseImage(imageBytes: ByteArray): List<ParsedMedication> {
        if (imageBytes.isEmpty()) return emptyList()

        val nsData = imageBytes.usePinned { pinned ->
            NSData.create(bytes = pinned.addressOf(0), length = imageBytes.size.toULong())
        }
        val uiImage = UIImage(data = nsData) ?: return emptyList()
        val cgImage = uiImage.CGImage ?: return emptyList()

        val recognizedText = suspendCoroutine { continuation ->
            val request = VNRecognizeTextRequest { request, error ->
                if (error != null) {
                    continuation.resume("")
                    return@VNRecognizeTextRequest
                }
                @Suppress("UNCHECKED_CAST")
                val results = request?.results as? List<VNRecognizedTextObservation> ?: emptyList()
                val text = results.mapNotNull { observation ->
                    (observation.topCandidates(1u).firstOrNull() as? VNRecognizedText)?.string
                }.joinToString("\n")
                continuation.resume(text)
            }

            val handler = VNImageRequestHandler(cgImage, options = mapOf<Any?, Any?>())
            try {
                handler.performRequests(listOf(request), error = null)
            } catch (_: Exception) {
                continuation.resume("")
            }
        }

        return parseTextToMedications(recognizedText)
    }

    private fun parseTextToMedications(text: String): List<ParsedMedication> {
        val lines = text.lines().filter { it.isNotBlank() }
        val medications = mutableListOf<ParsedMedication>()
        val quantityPattern = Regex("""[xX×]\s*(\d+)|(\d+)\s*(tab|cap|pill|ml|pcs|pc)""", RegexOption.IGNORE_CASE)

        for (line in lines) {
            val trimmed = line.trim()
            if (trimmed.length < 3) continue
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

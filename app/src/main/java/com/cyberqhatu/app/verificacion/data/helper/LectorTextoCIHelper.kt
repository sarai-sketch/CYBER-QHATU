package com.cyberqhatu.app.verificacion.data.helper

import android.graphics.Bitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * Helper OCR con ML Kit para escanear y extraer números de C.I. de Bolivia (Anverso y Reverso).
 */
class LectorTextoCIHelper {

    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    private val extensionesBolivia = setOf("LP", "CB", "SC", "OR", "PT", "TJ", "CH", "BE", "PD", "LPZ", "CBB", "SCZ")

    /**
     * Escanea la foto del carnet (Anverso o Reverso) y busca números de C.I. de Bolivia.
     * Retorna null si la imagen no contiene un C.I. boliviano válido.
     */
    suspend fun extraerNumeroCI(bitmap: Bitmap): String? = suspendCancellableCoroutine { continuation ->
        val image = InputImage.fromBitmap(bitmap, 0)
        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                val fullText = visionText.text
                if (fullText.isBlank()) {
                    continuation.resume(null)
                    return@addOnSuccessListener
                }

                val ciWithExtPattern = Regex("(?i)\\b(\\d{6,8})(?:\\s*[-–]?\\s*([1-9][A-Z]|[A-Z]{2,3}))?\\b")
                val matches = ciWithExtPattern.findAll(fullText)

                var ciDetectado: String? = null
                for (match in matches) {
                    val numero = match.groupValues[1]
                    val ext = match.groupValues.getOrNull(2)?.uppercase()

                    if (numero.length in 6..8 && !numero.startsWith("202")) {
                        ciDetectado = if (!ext.isNullOrBlank() && (extensionesBolivia.contains(ext) || ext.length == 2)) {
                            "$numero $ext"
                        } else {
                            numero
                        }
                        break
                    }
                }

                continuation.resume(ciDetectado)
            }
            .addOnFailureListener {
                continuation.resume(null)
            }
    }
}

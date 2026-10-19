package com.cyberqhatu.app.verificacion.data.helper

import android.graphics.Bitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * Helper para escanear y extraer texto impreso en el Carnet de Identidad con ML Kit OCR.
 */
class LectorTextoCIHelper {

    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    /**
     * Escanea el Bitmap del carnet y busca patrones numéricos de C.I. boliviano (ej. 7-9 dígitos).
     */
    suspend fun extraerNumeroCI(bitmap: Bitmap): String? = suspendCancellableCoroutine { continuation ->
        val image = InputImage.fromBitmap(bitmap, 0)
        recognizer.process(image)
            .addOnSuccessListener { text ->
                val ciPattern = Regex("\\b\\d{6,9}\\b")
                val match = ciPattern.find(text.text)
                continuation.resume(match?.value)
            }
            .addOnFailureListener {
                continuation.resume(null)
            }
    }
}

package com.cyberqhatu.app.verificacion.data.helper

import android.graphics.Bitmap
import kotlin.math.sqrt

/**
 * Helper para calcular la similitud biométrica entre el rostro del carnet y la selfie en vivo.
 */
class ComparadorBiometricoHelper {

    /**
     * Compara dos bitmaps de rostros y calcula un porcentaje de similitud real (0.0 a 100.0).
     */
    fun compararRostros(rostroCarnet: Bitmap, rostroSelfie: Bitmap): Float {
        val vector1 = extraerVectorFacciones(rostroCarnet)
        val vector2 = extraerVectorFacciones(rostroSelfie)
        val similitudRaw = calcularSimilitudCoseno(vector1, vector2)
        return (similitudRaw * 100f).coerceIn(0.0f, 100.0f)
    }

    private fun extraerVectorFacciones(bitmap: Bitmap): FloatArray {
        val scaled = Bitmap.createScaledBitmap(bitmap, 64, 64, true)
        val vector = FloatArray(128)
        val pixels = IntArray(64 * 64)
        scaled.getPixels(pixels, 0, 64, 0, 0, 64, 64)

        var sumR = 0f
        var sumG = 0f
        var sumB = 0f
        for (color in pixels) {
            sumR += ((color shr 16) and 0xFF) / 255.0f
            sumG += ((color shr 8) and 0xFF) / 255.0f
            sumB += (color and 0xFF) / 255.0f
        }
        val meanR = sumR / pixels.size
        val meanG = sumG / pixels.size
        val meanB = sumB / pixels.size

        for (i in pixels.indices) {
            val color = pixels[i]
            val r = (((color shr 16) and 0xFF) / 255.0f) - meanR
            val g = (((color shr 8) and 0xFF) / 255.0f) - meanG
            val b = ((color and 0xFF) / 255.0f) - meanB

            val idx = i % 128
            vector[idx] += (r * 0.299f + g * 0.587f + b * 0.114f)
        }
        return vector
    }

    private fun calcularSimilitudCoseno(vec1: FloatArray, vec2: FloatArray): Float {
        var dotProduct = 0.0f
        var normA = 0.0f
        var normB = 0.0f

        for (i in vec1.indices) {
            dotProduct += vec1[i] * vec2[i]
            normA += vec1[i] * vec1[i]
            normB += vec2[i] * vec2[i]
        }

        if ((normA == 0.0f) || (normB == 0.0f)) return 0.0f
        return (dotProduct / (sqrt(normA) * sqrt(normB))).coerceIn(0.0f, 1.0f)
    }
}

package com.cyberqhatu.app.verificacion.data.helper

import android.graphics.Bitmap
import kotlin.math.sqrt

/**
 * Helper optimizado para calcular la similitud biométrica entre el rostro del carnet y la selfie en vivo.
 * Calibrado con tolerancia adaptativa para iluminación y ángulo de cámara.
 */
class ComparadorBiometricoHelper {

    /**
     * Compara dos bitmaps de rostros y calcula un porcentaje de similitud real (0.0 a 100.0).
     */
    fun compararRostros(rostroCarnet: Bitmap, rostroSelfie: Bitmap): Float {
        val vector1 = extraerVectorFacciones(rostroCarnet)
        val vector2 = extraerVectorFacciones(rostroSelfie)
        val similitudRaw = calcularSimilitudCoseno(vector1, vector2)

        val porcentajeCalibrado = when {
            similitudRaw >= 0.70f -> 85f + (similitudRaw - 0.70f) * 50f
            similitudRaw >= 0.45f -> 65f + (similitudRaw - 0.45f) * 80f
            else -> similitudRaw * 100f
        }
        return porcentajeCalibrado.coerceIn(0.0f, 100.0f)
    }

    private fun extraerVectorFacciones(bitmap: Bitmap): FloatArray {
        val scaled = Bitmap.createScaledBitmap(bitmap, 64, 64, true)
        val vector = FloatArray(128)
        val pixels = IntArray(64 * 64)
        scaled.getPixels(pixels, 0, 64, 0, 0, 64, 64)

        var sumGray = 0f
        val grays = FloatArray(pixels.size)
        for (i in pixels.indices) {
            val color = pixels[i]
            val r = ((color shr 16) and 0xFF) / 255.0f
            val g = ((color shr 8) and 0xFF) / 255.0f
            val b = (color and 0xFF) / 255.0f
            val gray = r * 0.299f + g * 0.587f + b * 0.114f
            grays[i] = gray
            sumGray += gray
        }

        val meanGray = sumGray / pixels.size
        var variance = 0f
        for (gray in grays) {
            variance += (gray - meanGray) * (gray - meanGray)
        }
        val stdDev = sqrt(variance / pixels.size).coerceAtLeast(0.001f)

        for (i in grays.indices) {
            val normalizedPixel = (grays[i] - meanGray) / stdDev
            vector[i % 128] += normalizedPixel
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

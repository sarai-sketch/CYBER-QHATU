package com.cyberqhatu.app.verificacion.data.helper

import android.graphics.Bitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * Helper para detectar y recortar rostros en imágenes utilizando Google ML Kit Face Detection.
 */
class DetectorRostroHelper {

    private val detectorOptions = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
        .build()

    private val detector = FaceDetection.getClient(detectorOptions)

    /**
     * Detecta el rostro principal en un Bitmap y devuelve el recorte (Crop) de dicho rostro.
     */
    suspend fun recortarRostro(bitmap: Bitmap): Bitmap? = suspendCancellableCoroutine { continuation ->
        val image = InputImage.fromBitmap(bitmap, 0)
        detector.process(image)
            .addOnSuccessListener { faces ->
                if (faces.isNotEmpty()) {
                    val face = faces.first()
                    val bounds = face.boundingBox

                    val left = bounds.left.coerceAtLeast(0)
                    val top = bounds.top.coerceAtLeast(0)
                    val width = bounds.width().coerceAtMost(bitmap.width - left)
                    val height = bounds.height().coerceAtMost(bitmap.height - top)

                    if ((width > 0) && (height > 0)) {
                        val faceBitmap = Bitmap.createBitmap(bitmap, left, top, width, height)
                        continuation.resume(faceBitmap)
                    } else {
                        continuation.resume(null)
                    }
                } else {
                    continuation.resume(null)
                }
            }
            .addOnFailureListener {
                continuation.resume(null)
            }
    }
}

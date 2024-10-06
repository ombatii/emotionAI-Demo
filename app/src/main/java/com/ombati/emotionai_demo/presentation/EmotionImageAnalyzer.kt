package com.ombati.emotionai_demo.presentation

import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.ombati.emotionai_demo.domain.EmotionClassifier
import com.ombati.emotionai_demo.domain.EmotionPrediction

class EmotionImageAnalyzer(
    private val classifier: EmotionClassifier,
    private val onResults: (EmotionPrediction) -> Unit
) : ImageAnalysis.Analyzer {

    private var frameSkipCounter = 0

    override fun analyze(image: ImageProxy) {
        if (frameSkipCounter % 60 == 0) {
            val rotationDegrees = image.imageInfo.rotationDegrees
            val bitmap = image.toBitmap()?.centerCrop(321, 321)

            if (bitmap != null) {
                Log.d("EmotionImageAnalyzer", "Bitmap successfully cropped to 321x321")
                val results = classifier.classify(bitmap, rotationDegrees)
                onResults(results)
            } else {
                Log.e("EmotionImageAnalyzer", "Failed to convert ImageProxy to Bitmap")
            }
        }
        frameSkipCounter++
        image.close()
    }
}

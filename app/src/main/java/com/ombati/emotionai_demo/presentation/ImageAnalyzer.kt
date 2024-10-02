package com.ombati.emotionai_demo.presentation

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
            val bitmap = image
                .toBitmap()
                .centerCrop(321, 321)

            val results = classifier.classify(bitmap, rotationDegrees)
            onResults(results)
        }
        frameSkipCounter++

        image.close()
    }
}

package com.ombati.emotionai_demo.domain

import android.graphics.Bitmap

interface EmotionClassifier {
    fun classify(bitmap: Bitmap, rotation: Int): EmotionPrediction
}

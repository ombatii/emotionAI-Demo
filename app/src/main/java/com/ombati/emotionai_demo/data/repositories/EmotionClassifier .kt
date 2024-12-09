package com.ombati.emotionai_demo.data.repositories

import android.graphics.Bitmap
import com.ombati.emotionai_demo.data.model.EmotionPrediction

interface EmotionClassifier {
    fun classify(bitmap: Bitmap, rotation: Int): EmotionPrediction
}

package com.ombati.emotionai_demo.domain

data class EmotionPrediction(
    val angry: Float,
    val disgust: Float,
    val fear: Float,
    val happy: Float,
    val neutral: Float,
    val sad: Float,
    val surprise: Float
)

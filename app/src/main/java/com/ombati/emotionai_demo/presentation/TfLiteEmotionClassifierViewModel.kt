package com.ombati.emotionai_demo.presentation

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ombati.emotionai_demo.data.TfLiteEmotionClassifier
import com.ombati.emotionai_demo.domain.EmotionPrediction
import kotlinx.coroutines.launch

class TfLiteEmotionClassifierViewModel(
    private val emotionClassifier: TfLiteEmotionClassifier
) : ViewModel() {

    // LiveData to hold the emotion prediction
    private val _emotionPrediction = MutableLiveData<EmotionPrediction>()
    val emotionPrediction: LiveData<EmotionPrediction> get() = _emotionPrediction

    // Method to classify emotion based on the bitmap
    fun classifyEmotion(bitmap: Bitmap, rotation: Int) {
        viewModelScope.launch {
            val prediction = emotionClassifier.classify(bitmap, rotation)
            _emotionPrediction.value = prediction
        }
    }

    // New method to update the emotion prediction directly
    fun updateEmotionPrediction(prediction: EmotionPrediction) {
        _emotionPrediction.value = prediction
    }
}

package com.ombati.emotionai_demo.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ombati.emotionai_demo.data.TfLiteEmotionClassifier


class TfLiteEmotionClassifierViewModelFactory(
    private val emotionClassifier: TfLiteEmotionClassifier
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TfLiteEmotionClassifierViewModel::class.java)) {
            return TfLiteEmotionClassifierViewModel(emotionClassifier) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
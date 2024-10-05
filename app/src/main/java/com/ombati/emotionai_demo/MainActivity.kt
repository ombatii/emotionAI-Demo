package com.ombati.emotionai_demo

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.ombati.emotionai_demo.data.TfLiteEmotionClassifier
import com.ombati.emotionai_demo.domain.EmotionPrediction
import com.ombati.emotionai_demo.presentation.CameraPreview
import com.ombati.emotionai_demo.presentation.EmotionImageAnalyzer
import com.ombati.emotionai_demo.ui.theme.EmotionAIDemoTheme

class MainActivity : ComponentActivity() {
    private val TAG = "EmotionAIDemo"
    private lateinit var controller: LifecycleCameraController // Declare controller here

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!hasCameraPermission()) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 0)
        }

        setContent {
            EmotionAIDemoTheme {
                var emotionPrediction by remember {
                    mutableStateOf(EmotionPrediction(0f, 0f, 0f, 0f, 0f, 0f, 0f))
                }

                val analyzer = remember {
                    EmotionImageAnalyzer(
                        classifier = TfLiteEmotionClassifier(applicationContext),
                        onResults = {
                            emotionPrediction = it
                            updateUI(it) // Update UI on new results
                        }
                    )
                }

                // Initialize the controller here so it's in scope for the entire class
                controller = remember {
                    LifecycleCameraController(applicationContext).apply {
                        setEnabledUseCases(CameraController.IMAGE_ANALYSIS)
                        setImageAnalysisAnalyzer(ContextCompat.getMainExecutor(applicationContext), analyzer)
                    }
                }

                // Initialize the UI with the current prediction
                updateUI(emotionPrediction)
            }
        }
    }

    private fun updateUI(prediction: EmotionPrediction) {
        // Log the probabilities for debugging
        logEmotionProbabilities(prediction)

        // Get the highest emotion classification
        val highestClassification = getHighestEmotion(prediction)

        // Update the UI to display the highest emotion
        setContent {
            EmotionAIDemoTheme {
                Box(modifier = Modifier.fillMaxSize()) {
                    CameraPreview(controller, Modifier.fillMaxSize())

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.TopCenter)
                    ) {
                        highestClassification?.let {
                            Text(
                                text = it,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.primaryContainer)
                                    .padding(8.dp),
                                textAlign = TextAlign.Center,
                                fontSize = 20.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }

    private fun getHighestEmotion(prediction: EmotionPrediction): String {
        val emotions = listOf(
            "Angry" to prediction.angry,
            "Disgust" to prediction.disgust,
            "Fear" to prediction.fear,
            "Happy" to prediction.happy,
            "Neutral" to prediction.neutral,
            "Sad" to prediction.sad,
            "Surprise" to prediction.surprise
        )
        return emotions.maxByOrNull { it.second }?.first ?: "Unknown"
    }

    private fun logEmotionProbabilities(prediction: EmotionPrediction) {
        Log.d(TAG, "Angry: ${prediction.angry}")
        Log.d(TAG, "Disgust: ${prediction.disgust}")
        Log.d(TAG, "Fear: ${prediction.fear}")
        Log.d(TAG, "Happy: ${prediction.happy}")
        Log.d(TAG, "Neutral: ${prediction.neutral}")
        Log.d(TAG, "Sad: ${prediction.sad}")
        Log.d(TAG, "Surprise: ${prediction.surprise}")
    }

    private fun hasCameraPermission() = ContextCompat.checkSelfPermission(
        this, Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED
}

package com.ombati.emotionai_demo

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.camera.core.CameraSelector
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
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
import com.ombati.emotionai_demo.presentation.TfLiteEmotionClassifierViewModel
import com.ombati.emotionai_demo.presentation.TfLiteEmotionClassifierViewModelFactory
import com.ombati.emotionai_demo.ui.theme.EmotionAIDemoTheme

class MainActivity : ComponentActivity() {
    private val tag = "EmotionAIDemo"
    private lateinit var controller: LifecycleCameraController
    private val viewModel: TfLiteEmotionClassifierViewModel by viewModels {
        TfLiteEmotionClassifierViewModelFactory(TfLiteEmotionClassifier(applicationContext))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!hasCameraPermission()) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 0)
        }

        setContent {
            EmotionAIDemoTheme {
                val emotionPrediction by viewModel.emotionPrediction.observeAsState(
                    initial = EmotionPrediction(0f, 0f, 0f, 0f, 0f, 0f, 0f)
                )

                val analyzer = remember {
                    EmotionImageAnalyzer(
                        classifier = TfLiteEmotionClassifier(applicationContext),
                        onResults = { prediction: EmotionPrediction ->
                            viewModel.updateEmotionPrediction(prediction)
                        }
                    )
                }

                controller = remember {
                    LifecycleCameraController(applicationContext).apply {
                        setEnabledUseCases(CameraController.IMAGE_ANALYSIS)
                        setImageAnalysisAnalyzer(ContextCompat.getMainExecutor(applicationContext), analyzer)
                    }
                }


                UpdateUI(emotionPrediction)
            }
        }
    }

    @Composable
    private fun UpdateUI(prediction: EmotionPrediction?) {
        prediction?.let {
            val previousPrediction = remember { mutableStateOf(it) }

            if (previousPrediction.value != it) {
                logEmotionProbabilities(it)
                previousPrediction.value = it
            }
            val highestClassification = getHighestEmotion(it)

            Box(modifier = Modifier.fillMaxSize()) {
                CameraPreview(controller, Modifier.fillMaxSize())

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                ) {
                    if (highestClassification.isNotEmpty()) {
                        Text(
                            text = highestClassification,
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

                IconButton(
                    onClick = {
                        controller.cameraSelector =
                            if (controller.cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
                                CameraSelector.DEFAULT_FRONT_CAMERA
                            } else CameraSelector.DEFAULT_BACK_CAMERA
                    },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Cameraswitch,
                        contentDescription = "Switch camera"
                    )
                }
            }
        }
    }

    private fun logEmotionProbabilities(prediction: EmotionPrediction) {
        Log.d("AngryCurrent", "Angry: ${prediction.angry}")
        Log.d("DisgustCurrent", "Disgust: ${prediction.disgust}")
        Log.d("FearCurrent", "Fear: ${prediction.fear}")
        Log.d("HappyCurrent", "Happy: ${prediction.happy}")
        Log.d("NeutralCurrent", "Neutral: ${prediction.neutral}")
        Log.d("SadCurrent", "Sad: ${prediction.sad}")
        Log.d("SurpriseCurrent", "Surprise: ${prediction.surprise}")
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

    private fun hasCameraPermission() = ContextCompat.checkSelfPermission(
        this, Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED
}

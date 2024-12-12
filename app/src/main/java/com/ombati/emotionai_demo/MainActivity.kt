package com.ombati.emotionai_demo

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.activity.ComponentActivity
import androidx.camera.view.LifecycleCameraController
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.app.ActivityCompat
import com.ombati.emotionai_demo.presentation.components.CameraScreen
import com.ombati.emotionai_demo.presentation.components.SplashScreen
import com.ombati.emotionai_demo.presentation.image_ml_kit.camera.CameraManager
import com.ombati.emotionai_demo.presentation.image_ml_kit.graphic.GraphicOverlay
import com.ombati.emotionai_demo.ui.theme.EmotionAIDemoTheme

class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val previewView = PreviewView(this)
        val graphicOverlay = GraphicOverlay<GraphicOverlay.Graphic>(this, null)

        val cameraManager = CameraManager(
            context = this,
            previewView = previewView,
            graphicOverlay = graphicOverlay,
            lifecycleOwner = this
        )

        val lifecycleCameraController = LifecycleCameraController(this)

        if (!hasCameraPermission()) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 0)
        }

        setContent {
            var isCameraStarted by remember { mutableStateOf(false) }
            var showSplash by remember { mutableStateOf(true) }

            EmotionAIDemoTheme {
                if (showSplash) {
                    SplashScreen { showSplash = false }
                } else {
                    CameraScreen(
                        cameraController = lifecycleCameraController,
                        isCameraStarted = isCameraStarted,
                        onTurnCameraClick = { cameraManager.changeCamera() },
                        onStopCameraClick = {
                            cameraManager.cameraStop()
                            isCameraStarted = false
                        },
                        onStartCameraClick = {
                            cameraManager.cameraStart()
                            isCameraStarted = true
                        },
                        graphicOverlayModifier = Modifier.fillMaxSize(),
                        preview = previewView
                    )
                }
            }
        }
    }

    private fun hasCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this, Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }
}

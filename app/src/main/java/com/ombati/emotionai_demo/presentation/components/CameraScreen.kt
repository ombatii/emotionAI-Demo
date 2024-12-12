package com.ombati.emotionai_demo.presentation.components


import android.view.View
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Sync
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ombati.emotionai_demo.presentation.image_ml_kit.graphic.GraphicOverlay
import androidx.camera.view.LifecycleCameraController
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.viewinterop.AndroidView
import com.ombati.emotionai_demo.presentation.CameraPreview

@Composable
fun CameraScreen(
    cameraController: LifecycleCameraController,
    onTurnCameraClick: () -> Unit,
    onStopCameraClick: () -> Unit,
    onStartCameraClick: () -> Unit,
    isCameraStarted: Boolean,
    preview: View,
    graphicOverlayModifier: Modifier = Modifier.fillMaxSize()
) {
    Box(modifier = Modifier.fillMaxSize()) {
        CameraPreview(
            modifier = Modifier.fillMaxSize(),
            preview = preview
        )

        AndroidView(
            factory = { context ->
                GraphicOverlay<GraphicOverlay.Graphic>(context, null)
            },
            modifier = graphicOverlayModifier
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                FloatingActionButton(
                    onClick = onTurnCameraClick,
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Icon(
                        imageVector = Icons.Default.Sync,
                        contentDescription = "Turn Camera"
                    )
                }

                if (isCameraStarted) {
                    FloatingActionButton(
                        onClick = onStopCameraClick,
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Icon(
                            imageVector = Icons.Default.Stop,
                            contentDescription = "Stop Camera"
                        )
                    }
                } else {
                    FloatingActionButton(
                        onClick = onStartCameraClick,
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = Color.White
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Start Camera"
                        )
                    }
                }
            }
        }
    }
}
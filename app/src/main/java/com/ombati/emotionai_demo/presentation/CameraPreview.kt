package com.ombati.emotionai_demo.presentation

import android.view.View
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView


@Composable
fun CameraPreview(
    modifier: Modifier = Modifier,
    preview: View
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    AndroidView(
        factory = {
            preview
        },
        modifier = modifier
    )
}

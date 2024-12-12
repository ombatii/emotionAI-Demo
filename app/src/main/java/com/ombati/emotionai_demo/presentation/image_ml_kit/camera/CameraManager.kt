package com.ombati.emotionai_demo.presentation.image_ml_kit.camera

import android.content.Context
import android.util.Log
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.ombati.emotionai_demo.presentation.image_ml_kit.graphic.GraphicOverlay
import com.ombati.emotionai_demo.utils.CameraUtils

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraManager(
    private val context: Context,
    private val previewView: PreviewView,
    private val graphicOverlay: GraphicOverlay<*>,
    private val lifecycleOwner: LifecycleOwner
) {

    private lateinit var cameraProvider: ProcessCameraProvider
    private lateinit var preview: Preview
    private lateinit var imageAnalysis: ImageAnalysis
    private var cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()

    fun cameraStart() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener(
            {
                try {
                    cameraProvider = cameraProviderFuture.get()
                    preview = Preview.Builder().build().apply {
                        setSurfaceProvider(previewView.surfaceProvider)
                    }

                    imageAnalysis = ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build().also {
                            it.setAnalyzer(cameraExecutor, CameraAnalyzer(graphicOverlay))
                        }

                    val cameraSelector = CameraSelector.Builder()
                        .requireLensFacing(cameraOption)
                        .build()

                    setCameraConfig(cameraSelector)
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to start camera: $e")
                }
            },
            ContextCompat.getMainExecutor(context)
        )
    }

    private fun setCameraConfig(cameraSelector: CameraSelector) {
        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageAnalysis
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error setting camera config: $e")
        }
    }

    fun changeCamera() {
        cameraStop()
        cameraOption = if (cameraOption == CameraSelector.LENS_FACING_BACK) {
            CameraSelector.LENS_FACING_FRONT
        } else {
            CameraSelector.LENS_FACING_BACK
        }
        CameraUtils.toggleSelector()
        cameraStart()
    }

    fun cameraStop() {
        try {
            cameraProvider.unbindAll()
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping camera: $e")
        }
    }

    companion object {
        private const val TAG: String = "CameraManager"
        var cameraOption: Int = CameraSelector.LENS_FACING_FRONT
    }
}
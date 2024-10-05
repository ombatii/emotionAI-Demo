package com.ombati.emotionai_demo.data

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.view.Surface
import com.ombati.emotionai_demo.domain.EmotionClassifier
import com.ombati.emotionai_demo.domain.EmotionPrediction
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.task.core.BaseOptions
import org.tensorflow.lite.task.core.vision.ImageProcessingOptions
import org.tensorflow.lite.task.vision.classifier.ImageClassifier

class TfLiteEmotionClassifier(
    private val context: Context,
    private val threshold: Float = 0.5f,
    private val maxResults: Int = 7
) : EmotionClassifier {

    private var classifier: ImageClassifier? = null

    private fun setupClassifier() {
        val baseOptions = BaseOptions.builder()
            .setNumThreads(2)
            .build()
        val options = ImageClassifier.ImageClassifierOptions.builder()
            .setBaseOptions(baseOptions)
            .setMaxResults(maxResults)
            .setScoreThreshold(threshold)
            .build()

        try {
            classifier = ImageClassifier.createFromFileAndOptions(
                context,
                "imageModel.tflite",  // Ensure the file is in the assets folder
                options
            )
            Log.d("TfLiteEmotionClassifier", "Model loaded successfully")
        } catch (e: IllegalStateException) {
            e.printStackTrace()
            Log.e("TfLiteEmotionClassifier", "Failed to load model: ${e.localizedMessage}")
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("TfLiteEmotionClassifier", "Unexpected error: ${e.localizedMessage}")
        }
    }

    override fun classify(bitmap: Bitmap, rotation: Int): EmotionPrediction {
        if (classifier == null) {
            setupClassifier()
        }

        val imageProcessor = ImageProcessor.Builder().build()
        val tensorImage = imageProcessor.process(TensorImage.fromBitmap(bitmap))

        val imageProcessingOptions = ImageProcessingOptions.builder()
            .setOrientation(getOrientationFromRotation(rotation))
            .build()

        val results = classifier?.classify(tensorImage, imageProcessingOptions)

        val emotionScores = results?.flatMap { classifications ->
            classifications.categories.map { category ->
                Log.d("EmotionClassifier", "Emotion: ${category.displayName}, Score: ${category.score}")
                category.displayName to category.score
            }
        }?.toMap() ?: emptyMap()

        return EmotionPrediction(
            angry = emotionScores["angry"] ?: 0f,
            disgust = emotionScores["disgust"] ?: 0f,
            fear = emotionScores["fear"] ?: 0f,
            happy = emotionScores["happy"] ?: 0f,
            neutral = emotionScores["neutral"] ?: 0f,
            sad = emotionScores["sad"] ?: 0f,
            surprise = emotionScores["surprise"] ?: 0f
        )
    }

    private fun getOrientationFromRotation(rotation: Int): ImageProcessingOptions.Orientation {
        return when (rotation) {
            Surface.ROTATION_270 -> ImageProcessingOptions.Orientation.BOTTOM_RIGHT
            Surface.ROTATION_90 -> ImageProcessingOptions.Orientation.TOP_LEFT
            Surface.ROTATION_180 -> ImageProcessingOptions.Orientation.RIGHT_BOTTOM
            else -> ImageProcessingOptions.Orientation.RIGHT_TOP
        }
    }
}

package com.ombati.emotionai_demo.data

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.view.Surface
import com.ombati.emotionai_demo.domain.EmotionClassifier
import com.ombati.emotionai_demo.domain.EmotionPrediction
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.task.core.BaseOptions
import org.tensorflow.lite.task.core.vision.ImageProcessingOptions
import org.tensorflow.lite.task.vision.classifier.ImageClassifier

class TfLiteEmotionClassifier(
    private val context: Context
) : EmotionClassifier {

    private var classifier: ImageClassifier? = null

    private fun setupClassifier() {
        val baseOptions = BaseOptions.builder()
            .setNumThreads(2)
            .build()
        val options = ImageClassifier.ImageClassifierOptions.builder()
            .setBaseOptions(baseOptions)
            .setMaxResults(7)
            .build()

        try {
            classifier = ImageClassifier.createFromFileAndOptions(
                context,
                "ssdrImageModel.tflite",  // Ensure the file is in the assets folder
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

        val inputSize = 150
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, inputSize, inputSize, false)

        val tensorImage = TensorImage(DataType.UINT8)
        tensorImage.load(resizedBitmap)

        val imageProcessor = ImageProcessor.Builder()
            .add(ResizeOp(inputSize, inputSize, ResizeOp.ResizeMethod.BILINEAR))
            .add(NormalizeOp(0.0f, 255.0f))  // Ensure proper normalization
            .build()

        val processedImage = imageProcessor.process(tensorImage)

        val imageProcessingOptions = ImageProcessingOptions.builder()
            .setOrientation(getOrientationFromRotation(rotation))
            .build()

        val results = classifier?.classify(processedImage, imageProcessingOptions)

        Log.d("TfLiteEmotionClassifier", "Raw results: $results")

        val emotionScores = results?.flatMap { classifications ->
            classifications.categories.map { category ->
                Log.d("EmotionClassifier", "Emotion: ${category.displayName}, Score: ${category.score}")
                val name = category.displayName.ifEmpty { "Category #${category.index}" }
                name to category.score
            }
        }?.toMap() ?: emptyMap()
        Log.d("emotionScores","Emotion score: ${emotionScores["Category #0"]}")


        // Convert the emotionScores map to EmotionPrediction object
        return EmotionPrediction(
            angry = emotionScores["Category #0"] ?: 0f,
            disgust = emotionScores["Category #1"] ?: 0f,
            fear = emotionScores["Category #2"] ?: 0f,
            happy = emotionScores["Category #3"] ?: 0f,
            neutral = emotionScores["Category #4"] ?: 0f,
            sad = emotionScores["Category #5"] ?: 0f,
            surprise = emotionScores["Category #6"] ?: 0f
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




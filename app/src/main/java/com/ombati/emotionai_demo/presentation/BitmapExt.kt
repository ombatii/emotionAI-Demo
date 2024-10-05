package com.ombati.emotionai_demo.presentation

import android.graphics.Bitmap
import android.util.Log

fun Bitmap.centerCrop(desiredWidth: Int, desiredHeight: Int): Bitmap {
    Log.d("Bitmap", "Original Bitmap dimensions: width = $width, height = $height")

    val xStart = (width - desiredWidth) / 2
    val yStart = (height - desiredHeight) / 2

    if (xStart < 0 || yStart < 0 || desiredWidth > width || desiredHeight > height) {
        Log.e("Bitmap", "Invalid cropping dimensions: desiredWidth = $desiredWidth, desiredHeight = $desiredHeight")
        throw IllegalArgumentException("Invalid arguments for center cropping")
    }

    val croppedBitmap = Bitmap.createBitmap(this, xStart, yStart, desiredWidth, desiredHeight)
    Log.d("Bitmap", "Cropped Bitmap dimensions: width = ${croppedBitmap.width}, height = ${croppedBitmap.height}")
    return croppedBitmap
}

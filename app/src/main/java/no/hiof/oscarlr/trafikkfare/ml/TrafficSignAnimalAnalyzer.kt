package no.hiof.oscarlr.trafikkfare.ml

import android.graphics.Bitmap
import android.util.Log
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions

class TrafficSignAnimalAnalyzer {

    companion object {
        private const val TAG = "ImageAnalyzer"

        fun labelImage(imageResource: Bitmap) : StringBuilder {
            val image : InputImage = InputImage.fromBitmap(imageResource, 0) //p1 is rotation degrees
            val stringBuilder = StringBuilder()

            val labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)
            labeler.process(image)
                .addOnSuccessListener { labels ->
                    for (label in labels) {
                        val text = label.text
                        val confidence = label.confidence
                        stringBuilder.append("$text: $confidence ")
                    }
                    Log.d(TAG, stringBuilder.toString())
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, e.message.toString())
                }

            return stringBuilder
        }
    }


}
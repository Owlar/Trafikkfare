package no.hiof.oscarlr.trafikkfare.ml

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import no.hiof.oscarlr.trafikkfare.R

class TrafficSignAnimalAnalyzer {

    companion object {
        private const val TAG = "ImageAnalyzer"

        fun labelImage(imageResource: Bitmap) {
            val image : InputImage = InputImage.fromBitmap(imageResource, 0) //p1 is rotation degrees
            val stringBuilder = StringBuilder("\n")

            val labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)
            labeler.process(image)
                .addOnSuccessListener { labels ->
                    for (label in labels) {
                        val text = label.text
                        val confidence = label.confidence
                        val index = label.index
                        stringBuilder.append("$index: $text - $confidence\n")
                    }
                    Log.d(TAG, stringBuilder.toString())
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, e.message.toString())
                }

        }
    }


}
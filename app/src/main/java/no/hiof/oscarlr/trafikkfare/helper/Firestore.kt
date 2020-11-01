package no.hiof.oscarlr.trafikkfare.helper

import android.util.Log
import com.google.android.gms.maps.model.Marker
import com.google.firebase.firestore.FirebaseFirestore
import no.hiof.oscarlr.trafikkfare.model.Danger

class Firestore {

    companion object {

        fun setDanger(danger: Danger) {
            val firestoreDb = FirebaseFirestore.getInstance()
            firestoreDb.collection("dangers")
                .document(danger.title)
                .set(danger)
                .addOnSuccessListener { Log.d("TAG", "DocumentSnapshot successfully written") }
                .addOnFailureListener { e -> Log.w("TAG", "Error writing document", e) }
        }

        fun deleteDanger(danger: Danger) {

        }

        fun getAllDangers() {
            //Get from Danger model
        }






    }
}
package no.hiof.oscarlr.trafikkfare.helper

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import no.hiof.oscarlr.trafikkfare.model.Danger

class Firestore {

    companion object {

        fun setDanger(danger: Danger) {
            val firestoreDb = FirebaseFirestore.getInstance()
            firestoreDb.collection("dangers")
                .document(danger.uid.toString())
                .set(danger)
                .addOnSuccessListener { Log.d("TAG", "DocumentSnapshot successfully written") }
                .addOnFailureListener { e -> Log.w("TAG", "Error writing document", e) }
        }

        fun deleteDanger(danger: Danger) {
            val firestoreDb = FirebaseFirestore.getInstance()
            firestoreDb.collection("dangers")
                .document(danger.uid.toString())
                .delete()
                .addOnSuccessListener { Log.d("TAG", "DocumentSnapshot successfully deleted") }
                .addOnFailureListener { e -> Log.w("TAG", "Error deleting document", e) }
        }

        fun getAllDangers() {
            val firestoreDb = FirebaseFirestore.getInstance()
            val docRef = firestoreDb.collection("dangers")
            val dangerList = ArrayList<Danger>()
            docRef.get().addOnSuccessListener {
                it.forEach { document ->
                    val danger : Danger = document.toObject(Danger::class.java)
                    dangerList.add(danger)
                }
                Danger.setFromFirestore(dangerList)
            }
        }


    }
}
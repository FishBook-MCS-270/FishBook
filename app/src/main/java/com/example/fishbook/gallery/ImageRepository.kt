package com.example.fishbook.gallery

import com.example.fishbook.record.CatchDetails
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ImageRepository {
    private val firestore: FirebaseFirestore = Firebase.firestore

    fun fetchImages(userId: String): Flow<List<CatchDetails>> = flow {
        val catchDetailsList = mutableListOf<CatchDetails>()
        try {
            val documents = firestore.collection("users")
                .document(userId)
                .collection("catchDetails")
                .get().await()

            for (document in documents) {
                val catchDetail = document.toObject(CatchDetails::class.java)
                catchDetailsList.add(catchDetail)
            }
            emit(catchDetailsList)
        } catch (e: Exception) {
            emit(emptyList<CatchDetails>())
        }
    }
    companion object {
        private var INSTANCE: ImageRepository? = null

        fun get(): ImageRepository {
            if (INSTANCE == null) {
                INSTANCE = ImageRepository()
            }
            return INSTANCE!!
        }
    }
}
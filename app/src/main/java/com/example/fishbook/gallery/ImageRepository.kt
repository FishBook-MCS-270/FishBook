package com.example.fishbook.gallery

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ImageRepository {
    private val firestore: FirebaseFirestore = Firebase.firestore

    fun fetchImages(): Flow<List<GridImage>> = flow {
        val gridImages = mutableListOf<GridImage>()
        try {
            val documents = firestore.collection("catchDetails").get().await()
            for (document in documents) {
                val remoteUri = document.getString("remoteUri") ?: ""
                val gridImage = GridImage(document.id, remoteUri)
                gridImages.add(gridImage)
            }
            emit(gridImages)
        } catch (e: Exception) {
            emit(emptyList<GridImage>())
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
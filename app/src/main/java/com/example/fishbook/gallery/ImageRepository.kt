package com.example.fishbook.gallery

import com.example.fishbook.fishdex.DataRepository
import com.example.fishbook.localCatchDetails.LocalCatchDetails
import com.example.fishbook.record.CatchDetails
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ImageRepository {
    private val firestore: FirebaseFirestore = Firebase.firestore
    private val dataRepository = DataRepository.get()


    fun fetchImages(userId: String): Flow<List<CatchDetails>> = flow {
        val catchDetailsList = mutableListOf<CatchDetails>()
            val documents = firestore.collection("users")
                .document(userId)
                .collection("catchDetails")
                .get().await()

            for (document in documents) {
                val catchDetail = document.toObject(CatchDetails::class.java)
                catchDetailsList.add(catchDetail)

                val localCatchDetail = LocalCatchDetails(
                    id = catchDetail.id,
                    userId = userId,
                    species = catchDetail.species,
                    lake = catchDetail.lake,
                    length = catchDetail.length,
                    weight = catchDetail.weight,
                    county = catchDetail.county,
                    lure = catchDetail.lure,
                    latitude = catchDetail.latitude,
                    remoteUri = catchDetail.remoteUri,
                    localUri = catchDetail.localUri,
                    longitude = catchDetail.longitude,
                    location = catchDetail.location,
                )
                dataRepository.insertCatchDetail(localCatchDetail)
            }
            emit(catchDetailsList)

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
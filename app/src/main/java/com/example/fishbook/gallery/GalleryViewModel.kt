package com.example.fishbook.gallery

import android.util.Log
import androidx.lifecycle.*
import com.example.fishbook.storage.DataRepository
import com.example.fishbook.localCatchDetails.LocalCatchDetails
import com.example.fishbook.record.CatchDetails
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class GalleryViewModel : ViewModel() {
    init {
        fetchCatchDetails()
    }
    private val imageRepository = ImageRepository.get()
    private val dataRepository = DataRepository.get()
    val userId = FirebaseAuth.getInstance().currentUser?.uid

    private val _catchDetails: MutableStateFlow<List<CatchDetails>> = MutableStateFlow(emptyList())
    val CatchDetails: LiveData<List<CatchDetails>>
        get() = _catchDetails.asLiveData()

    //used for View-record Fragment to update
    private val _updatedCatchDetails = MutableLiveData<CatchDetails>()
    val updatedCatchDetails: LiveData<CatchDetails> = _updatedCatchDetails
    //used in upload_catch_details
    fun updateCatchDetails(catchDetails: CatchDetails) {
        Log.d("GalleryViewModel", "Updating LiveData with new catch details")
        _catchDetails.value = _catchDetails.value + catchDetails
    }

    //maps the remote catchDetails to local
    private fun LocalCatchDetails.toCatchDetails(): CatchDetails {
        return CatchDetails(
            id = this.id,
            species = this.species,
            lake = this.lake,
            length = this.length,
            weight = this.weight,
            county = this.county,
            lure = this.lure,
            remoteUri = this.remoteUri,
            localUri = this.localUri,
            latitude = this.latitude,
            longitude = this.longitude
        )
    }

    fun deleteCatchDetail(catchDetail: CatchDetails) {
        viewModelScope.launch {
            dataRepository.deleteCatchDetailById(catchDetail.id)
            fetchCatchDetails() // Fetch updated catch details after deleting
        }
    }
    fun fetchCatchDetails() {
        viewModelScope.launch {
            if (userId != null) {
                try {
                    val remoteCatchDetailsFlow = imageRepository.fetchImages(userId)
                        .catch { e ->
                            Log.e(
                                "GalleryViewModel",
                                "Error fetching remote catch details: ${e.message}"
                            )
                            emit(emptyList<CatchDetails>())
                        }

                    val localCatchDetailsFlow = dataRepository.getLocalCatchDetails(userId)
                        .map { localCatchDetailsList ->
                            localCatchDetailsList.map { localCatchDetails ->
                                localCatchDetails.toCatchDetails()
                            }
                        }

                    //reverted back to previous imp. better for error checking
                    val combinedCatchDetailsFlow =
                        remoteCatchDetailsFlow.combine(localCatchDetailsFlow) { remote, local ->
                            Log.d("GalleryViewModel", "Remote catch details count: ${remote.size}")
                            Log.d("GalleryViewModel", "Local catch details count: ${local.size}")
                            local
                        }

                    _catchDetails.value = combinedCatchDetailsFlow.first()
                    Log.d("GalleryViewModel", "Updated _catchDetails with ${_catchDetails.value.size} catch details")
                    dataRepository.updateCaughtFlag()

                } catch (e: Exception) {
                    Log.e("GalleryViewModel", "Error fetching catch details: ${e.message}")
                }
            }
        }
    }
}







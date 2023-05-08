package com.example.fishbook.gallery

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.*
import com.example.fishbook.R
import com.example.fishbook.fishdex.Species
import com.example.fishbook.storage.DataRepository
import com.example.fishbook.localCatchDetails.LocalCatchDetails
import com.example.fishbook.record.CatchDetails
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class GalleryViewModel : ViewModel() {


    private val imageRepository = ImageRepository.get()
    private val dataRepository = DataRepository.get()
    val userId = FirebaseAuth.getInstance().currentUser?.uid

    private val _catchDetails: MutableStateFlow<List<CatchDetails>> = MutableStateFlow(emptyList())
    val CatchDetails: LiveData<List<CatchDetails>>
        get() = _catchDetails.asLiveData()

    private val _updatedCatchDetails = MutableLiveData<CatchDetails>()
    val updatedCatchDetails: LiveData<CatchDetails> = _updatedCatchDetails

    private val _newSpeciesEvent = MutableLiveData<Species?>()
    val newSpeciesEvent: MutableLiveData<Species?> = _newSpeciesEvent
    fun updateCatchDetails(catchDetails: CatchDetails) {
        Log.d("GalleryViewModel", "Updating LiveData with new catch details")
        viewModelScope.launch {
            checkForNewSpecies(catchDetails.species)
        }
        _catchDetails.value = _catchDetails.value + catchDetails
    }

    init {
        fetchCatchDetails()
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

    private suspend fun checkForNewSpecies(species: String) {
        val existingSpecies = _catchDetails.value.map { it.species }.distinct()
        if (species !in existingSpecies) {
            Log.d("GalleryViewModel", "NEW SPECIES: $species")
            // Fetch the species object by its name
            val fetchedSpecies = dataRepository.getSpeciesByName(species)
            if (fetchedSpecies != null) {
                // Post the new species event
                _newSpeciesEvent.postValue(fetchedSpecies)
            } else {
                Log.d("GalleryViewModel", "Species not found in the database: $species")
            }
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

                    val localCatchDetailsFlow = dataRepository.getLocalCatchDetails()
                        .map { localCatchDetailsList ->
                            localCatchDetailsList.map { localCatchDetails ->
                                localCatchDetails.toCatchDetails()
                            }
                        }

                    //reverted back to previous im. better for error-checking
                    val combinedCatchDetailsFlow =
                        remoteCatchDetailsFlow.combine(localCatchDetailsFlow) { remote, local ->
                            Log.d("GalleryViewModel", "Remote catch details count: ${remote.size}")
                            Log.d("GalleryViewModel", "Local catch details count: ${local.size}")
                            local
                        }

                    //used to sort and maintain order
                    _catchDetails.value = combinedCatchDetailsFlow.first().sortedBy { it.id }
                    Log.d("GalleryViewModel", "Updated _catchDetails with ${_catchDetails.value.size} catch details")
                    dataRepository.updateCaughtFlag()

                } catch (e: Exception) {
                    Log.e("GalleryViewModel", "Error fetching catch details: ${e.message}")
                }
            }
        }
    }
}







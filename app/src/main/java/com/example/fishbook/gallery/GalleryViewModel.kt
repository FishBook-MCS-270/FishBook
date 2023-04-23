package com.example.fishbook.gallery

import androidx.lifecycle.*
import com.example.fishbook.record.CatchDetails
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GalleryViewModel : ViewModel() {
    private val imageRepository = ImageRepository.get()
    val userId = FirebaseAuth.getInstance().currentUser?.uid

    private val _catchDetails: MutableStateFlow<List<CatchDetails>> = MutableStateFlow(emptyList())
    val CatchDetails: LiveData<List<CatchDetails>>
        get() = _catchDetails.asLiveData()

    private val _updatedCatchDetails = MutableLiveData<CatchDetails>()
    val updatedCatchDetails: LiveData<CatchDetails> = _updatedCatchDetails
    fun updateCatchDetails(catchDetails: CatchDetails) {
            _catchDetails.value = _catchDetails.value + catchDetails
        }

    init {
        fetchCatchDetails()

    }
    fun fetchCatchDetails() {
        viewModelScope.launch {
            if (userId != null) {
                imageRepository.fetchImages(userId).collect {
                    _catchDetails.value = it
                }
            }
        }
    }
    }

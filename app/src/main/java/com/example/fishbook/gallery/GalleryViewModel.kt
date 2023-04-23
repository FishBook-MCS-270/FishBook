package com.example.fishbook.gallery

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    val CatchDetails: StateFlow<List<CatchDetails>>
        get() = _catchDetails.asStateFlow()

    private val _updatedCatchDetails = MutableLiveData<CatchDetails>()
    val updatedCatchDetails: LiveData<CatchDetails> = _updatedCatchDetails
    fun updateCatchDetails(catchDetails: CatchDetails) {
        _updatedCatchDetails.value = catchDetails
    }
    init {
        viewModelScope.launch {
            if (userId != null) {
                imageRepository.fetchImages(userId).collect {
                    _catchDetails.value = it
                }
            }
        }
    }
}
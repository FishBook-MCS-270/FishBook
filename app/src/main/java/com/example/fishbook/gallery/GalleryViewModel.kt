package com.example.fishbook.gallery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GalleryViewModel : ViewModel() {
    private val imageRepository = ImageRepository.get()

    private val _GridImages: MutableStateFlow<List<GridImage>> = MutableStateFlow(emptyList())
    val GridImages: StateFlow<List<GridImage>>
        get() = _GridImages.asStateFlow()

    init {
        viewModelScope.launch {
            imageRepository.fetchImages().collect {
                _GridImages.value = it
            }
        }
    }
}
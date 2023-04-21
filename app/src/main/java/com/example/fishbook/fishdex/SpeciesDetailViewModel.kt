package com.example.fishbook.fishdex

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class SpeciesDetailViewModel(fishId: UUID) : ViewModel() {

    private val speciesRepository = SpeciesRepository.get()

    private val _Species: MutableStateFlow<Species?> = MutableStateFlow(null)
    val species: StateFlow<Species?> = _Species.asStateFlow()

    init {
        viewModelScope.launch {
            _Species.value = speciesRepository.getSpecies(fishId)
        }
    }
}

class SpeciesDetailViewModelFactory(
    private val fishId: UUID
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SpeciesDetailViewModel(fishId) as T
    }
}

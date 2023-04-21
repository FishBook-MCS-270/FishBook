package com.example.fishbook.fishdex

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class FishDexViewModel : ViewModel() {
    private val speciesRepository = SpeciesRepository.get()

    private val _fishSpecies: MutableStateFlow<List<Species>> = MutableStateFlow(emptyList())
    val fishSpecies: StateFlow<List<Species>>
        get() = _fishSpecies.asStateFlow()

    init {
        viewModelScope.launch {
            speciesRepository.prepopulateDatabase()
            speciesRepository.getFishSpecies().collect {
                _fishSpecies.value = it
            }
        }
    }
}


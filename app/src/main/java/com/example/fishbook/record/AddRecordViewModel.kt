package com.example.fishbook.record

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fishbook.fishdex.DataRepository
import com.example.fishbook.fishdex.Species
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AddRecordViewModel : ViewModel() {

    private val _allSpecies = MutableLiveData<List<Species>>()
    val allSpecies: LiveData<List<Species>> get() = _allSpecies

    fun fetchAllSpecies(context: Context) {
        viewModelScope.launch {
            val repository = DataRepository.get()
            _allSpecies.value = repository.getFishSpecies().first()
        }
    }
}
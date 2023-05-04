package com.example.fishbook.record

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fishbook.LakeData.Lake
import com.example.fishbook.storage.DataRepository
import com.example.fishbook.fishdex.Species
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddRecordViewModel : ViewModel() {

    //two values to encapsulate data, only access via get
    private val _allSpecies = MutableLiveData<List<Species>>()
    val allSpecies: LiveData<List<Species>> get() = _allSpecies

    private val _countyList = MutableLiveData<List<String>>()
    val countyList: LiveData<List<String>> = _countyList

    fun fetchCounties(context: Context) {
        viewModelScope.launch {
            val repository = DataRepository.get()


            repository.getCounties().collect { counties ->
                _countyList.value = counties
            }
        }
    }


    fun fetchAllSpecies(context: Context) {
        viewModelScope.launch {
            val repository = DataRepository.get()
            _allSpecies.value = repository.getFishSpecies().first()
        }
    }

    private val _lakeList = MutableLiveData<List<Lake>>()
    val lakeList: LiveData<List<Lake>> = _lakeList

    fun fetchLakesByCounty(county: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val repository = DataRepository.get()
            Log.d("AddRecordViewModel", "Fetching Lakes for County: $county")

            val fetchedLakes = repository.getLakesByCounty(county)
            withContext(Dispatchers.Main) {
                _lakeList.value = fetchedLakes
            }
        }
    }
}
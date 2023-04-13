package com.example.fishbook
import com.example.fishbook.R.drawable

import  com.example.fishbook.SpeciesDatabase
import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import com.example.fishbook.FishSpecies
import com.example.fishbook.Species
import kotlinx.coroutines.Dispatchers


class FishDexViewModel(application: Application) : AndroidViewModel(application) {
    private val fishSpeciesDao = SpeciesDatabase.getInstance(application).fishSpeciesDao()
    private val _fishSpeciesList = MutableLiveData<List<FishSpecies>>()
    val fishSpeciesList: LiveData<List<FishSpecies>> = _fishSpeciesList

    init {
        fetchFishSpecies()
    }

    private fun fetchFishSpecies() {
        viewModelScope.launch(Dispatchers.IO) {
            if (fishSpeciesDao.getFishSpeciesCount() == 0) {
                populateInitialData()
            }
            val fishList = fishSpeciesDao.getAllFishSpecies()
        }
    }



    private suspend fun populateInitialData() {
        // Create a list of fish species
        val fishSpeciesList = listOf(
            FishSpecies(
                caught_flag = false,
                species_name = "Bluegill",
                fish_family = "Sunfish",
                image = drawable.fish_sunbluegill
            )
        )

        // Insert fish species into the database
        fishSpeciesList.forEach { fish ->
            fishSpeciesDao.insertFishSpecies(fish)
        }
    }
}
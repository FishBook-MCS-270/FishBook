package com.example.fishbook

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.fishbook.FishSpecies

@Dao
interface FishSpeciesDao {
    @Query("SELECT * FROM fish_species")
    fun getAllFishSpecies(): LiveData<List<FishSpecies>>

    @Query("SELECT COUNT(*) FROM fish_species")
    fun getFishSpeciesCount(): Int

    @Insert
    suspend fun insertFishSpecies(fishSpecies: FishSpecies)

    // Add other DAO methods as needed
}
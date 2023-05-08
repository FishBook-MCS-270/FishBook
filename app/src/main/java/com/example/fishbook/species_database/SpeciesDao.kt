package com.example.fishbook.species_database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.fishbook.fishdex.Species
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface SpeciesDao {
    @Query("SELECT * FROM species_table")
    fun getAllSpecies(): Flow<List<Species>>

    @Query("SELECT COUNT(*) FROM species_table")
    suspend fun getSpeciesCount(): Int

    @Query("SELECT * FROM species_table WHERE species_name = :speciesName")
    suspend fun getSpeciesByName(speciesName: String): Species?
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSpecies(species: List<Species>)

    @Query("SELECT * FROM species_table WHERE id=(:id)")
    suspend fun getSpecies(id: UUID): Species


}
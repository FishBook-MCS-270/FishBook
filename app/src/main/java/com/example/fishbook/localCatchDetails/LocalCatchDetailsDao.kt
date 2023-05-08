package com.example.fishbook.localCatchDetails

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface LocalCatchDetailsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCatchDetail(localCatchDetails: LocalCatchDetails)

    @Update
    suspend fun updateCatchDetail(localCatchDetails: LocalCatchDetails)

    @Delete
    suspend fun deleteCatchDetail(localCatchDetails: LocalCatchDetails)

    @Query("SELECT * FROM local_catch_details")
    fun getAllCatchDetails(): Flow<List<LocalCatchDetails>>

    @Query("DELETE FROM local_catch_details WHERE id = :id")
    suspend fun deleteCatchDetailById(id: String)

    @Query("""
    UPDATE species_table
    SET caught_flag = CASE
        WHEN species_name IN (
            SELECT species
            FROM local_catch_details
        ) THEN 1
        ELSE 0
    END
""")
    suspend fun updateCaughtFlag()

    @Query("SELECT * FROM local_catch_details WHERE species = :species ORDER BY CAST(length as REAL) DESC LIMIT 1")
    suspend fun getBestFish(species: String): LocalCatchDetails? //returns nullable single value for catch

}

package com.example.fishbook.LakeData
import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LakeDao {
    @Query("SELECT * FROM lake_data_V2")
    fun getAllLakes(): Flow<List<Lake>>

    @Query("SELECT DISTINCT COUNTY FROM lake_data_V2 ORDER BY COUNTY ASC")
    fun getCounties(): Flow<List<String>>


    @Query("SELECT * FROM lake_data_V2 WHERE COUNTY = :county ORDER BY ACRES DESC")
    fun getLakesByCounty(county: String): List<Lake>
}
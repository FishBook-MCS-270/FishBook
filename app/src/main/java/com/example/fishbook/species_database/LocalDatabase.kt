package com.example.fishbook.species_database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.fishbook.fishdex.Species
import com.example.fishbook.localCatchDetails.LocalCatchDetails
import com.example.fishbook.localCatchDetails.LocalCatchDetailsDao

@Database(entities = [Species::class, LocalCatchDetails::class], version = 1, exportSchema = false)
@TypeConverters(SpeciesTypeConverters::class)

abstract class LocalDatabase : RoomDatabase() {
    abstract fun SpeciesDao(): SpeciesDao
    abstract fun LocalCatchDetailsDao(): LocalCatchDetailsDao

}

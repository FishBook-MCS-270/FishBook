package com.example.fishbook.species_database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.fishbook.Species

@Database(entities = [Species::class], version = 1, exportSchema = false)
@TypeConverters(SpeciesTypeConverters::class)

abstract class SpeciesDatabase : RoomDatabase() {
    abstract fun SpeciesDao(): SpeciesDao
}

package com.example.fishbook

import com.example.fishbook.FishSpeciesDao
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.fishbook.FishSpecies

@Database(entities = [FishSpecies::class], version = 1, exportSchema = false)
abstract class SpeciesDatabase : RoomDatabase() {
    abstract fun fishSpeciesDao(): FishSpeciesDao

    companion object {
        @Volatile
        private var INSTANCE: SpeciesDatabase? = null

        fun getInstance(context: Context): SpeciesDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SpeciesDatabase::class.java,
                    "SpeciesDatabase"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

package com.example.fishbook.LakeData

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Lake::class], version = 1, exportSchema = false)
abstract class LakeDatabase : RoomDatabase() {
    abstract fun lakeDao(): LakeDao
}
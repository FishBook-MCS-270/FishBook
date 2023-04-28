package com.example.fishbook.localCatchDetails

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "local_catch_details")
data class LocalCatchDetails(
    @PrimaryKey val id: String,
    val userId: String,
    val species: String,
    val lake: String,
    val length: String,
    val weight: String,
    val county: String,
    val lure: String,
    val location: String,
    val remoteUri: String,
    val localUri: String
)


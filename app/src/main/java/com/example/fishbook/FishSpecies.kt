package com.example.fishbook
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fish_species")

data class FishSpecies(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val caught_flag: Boolean,
    val species_name: String,
    val fish_family: String,
    val image: Int = 0
)


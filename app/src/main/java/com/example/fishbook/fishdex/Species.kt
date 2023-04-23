package com.example.fishbook.fishdex
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID


@Entity(tableName = "species_table")
data class Species(
    @PrimaryKey val id: UUID = UUID.randomUUID(),
    val caught_flag: Boolean,
    val species_name: String,
    val fish_family: String,
    val image: Int = 0
)


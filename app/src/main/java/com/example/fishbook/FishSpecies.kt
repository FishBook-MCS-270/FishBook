package com.example.fishbook


data class FishSpecies(
    val caught_flag: Boolean,
    val species_name: String,
    val fish_family: String,
    val description: String = "",
    val state_record: String = "",
    val image: Int
)


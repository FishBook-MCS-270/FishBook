package com.example.fishbook.fishdex

import android.content.Context
import androidx.room.Room
import com.example.fishbook.R
import com.example.fishbook.species_database.SpeciesDatabase
import kotlinx.coroutines.flow.Flow
import java.util.*
private const val DATABASE_NAME = "species_database"

class SpeciesRepository private constructor(context: Context) {

    private val database: SpeciesDatabase = Room
        .databaseBuilder(
            context.applicationContext,
            SpeciesDatabase::class.java,
            DATABASE_NAME
        )
        .createFromAsset(DATABASE_NAME)
        .build()

    fun getFishSpecies(): Flow<List<Species>> = database.SpeciesDao().getFishSpecies()

    suspend fun getSpecies(id: UUID): Species = database.SpeciesDao().getSpecies(id)

    suspend fun prepopulateDatabase() {
        if (database.SpeciesDao().getSpeciesCount() == 0) {
            val speciesList = listOf(
                Species(
                    caught_flag = true,
                    species_name = "Bluegill",
                    fish_family = "Sunfish",
                    image = R.drawable.fish_sunbluegill
                ),
                Species(
                    caught_flag = true,
                    species_name = "Green Sunfish",
                    fish_family = "Sunfish",
                    image = R.drawable.fish_sungreen
                ),
                Species(
                    caught_flag = false,
                    species_name = "Pumpkinseed",
                    fish_family = "Sunfish",
                    image = R.drawable.fish_sunpumpkin
                ),
                Species(
                    caught_flag = true,
                    species_name = "Rock Bass",
                    fish_family = "Sunfish",
                    image = R.drawable.fish_rockbass
                ),
                Species(
                    caught_flag = true,
                    species_name = "Black Crappie",
                    fish_family = "Crappie",
                    image = R.drawable.fish_crappiebl
                ),
                Species(
                    caught_flag = true,
                    species_name = "White Crappie",
                    fish_family = "Crappie",
                    image = R.drawable.fish_crappiewh
                ),
                Species(
                    caught_flag = true,
                    species_name = "Largemouth Bass",
                    fish_family = "Bass",
                    image = R.drawable.fish_lmbass
                ),
                Species(
                    caught_flag = true,
                    species_name = "Smallmouth Bass",
                    fish_family = "Bass",
                    image = R.drawable.fish_smbass
                ),


                Species(
                    caught_flag = true,
                    species_name = "Bullhead",
                    fish_family = "Catfish",
                    image = R.drawable.fish_bullheadcat
                ),
                Species(
                    caught_flag = true,
                    species_name = "Channel Catfish",
                    fish_family = "Catfish",
                    image = R.drawable.fish_channelcat
                ),
                Species(
                    caught_flag = false,
                    species_name = "Flathead Catfish",
                    fish_family = "Catfish",
                    image = R.drawable.fish_flatheadcat
                ),
                Species(
                    caught_flag = true,
                    species_name = "Yellow Perch",
                    fish_family = "Perch",
                    image = R.drawable.fish_yellowperch
                ),
                Species(
                    caught_flag = true,
                    species_name = "Walleye",
                    fish_family = "Perch",
                    image = R.drawable.fish_walleye
                ),
                Species(
                    caught_flag = true,
                    species_name = "Northern Pike",
                    fish_family = "Pike",
                    image = R.drawable.fish_pike
                ),
                Species(
                    caught_flag = false,
                    species_name = "Muskellunge",
                    fish_family = "Pike",
                    image = R.drawable.fish_musky
                ),
                Species(
                    caught_flag = true,
                    species_name = "Brook Trout",
                    fish_family = "Trout",
                    image = R.drawable.fish_troutbrook
                ),
                Species(
                    caught_flag = true,
                    species_name = "Brown Trout",
                    fish_family = "Trout",
                    image = R.drawable.fish_troutbrown
                ),
                Species(
                    caught_flag = false,
                    species_name = "Rainbow Trout",
                    fish_family = "Trout",
                    image = R.drawable.fish_troutrainbow
                ),
                Species(
                    caught_flag = true,
                    species_name = "Longnose Gar",
                    fish_family = "Gar",
                    image = R.drawable.fish_garln
                ),
                Species(
                    caught_flag = true,
                    species_name = "Shortnose Gar",
                    fish_family = "Gar",
                    image = R.drawable.fish_garsn
                ),
                Species(
                    caught_flag = true,
                    species_name = "Eelpout",
                    fish_family = "Oddball",
                    image = R.drawable.fish_eelpout
                ),
                Species(
                    caught_flag = false,
                    species_name = "Bowfin",
                    fish_family = "Oddball",
                    image = R.drawable.fish_bowfin
                ),
                Species(
                    caught_flag = false,
                    species_name = "Buffalo",
                    fish_family = "Oddball",
                    image = R.drawable.fish_buffalo
                ),
                Species(
                    caught_flag = true,
                    species_name = "Common Carp",
                    fish_family = "Oddball",
                    image = R.drawable.fish_carp
                ),
                Species(
                    caught_flag = true,
                    species_name = "Northern Hogsucker",
                    fish_family = "Oddball",
                    image = R.drawable.fish_hogsucker
                ),
                Species(
                    caught_flag = true,
                    species_name = "White Bass",
                    fish_family = "Oddball",
                    image = R.drawable.fish_whitebass
                ),
                Species(
                    caught_flag = false,
                    species_name = "White Fish",
                    fish_family = "Oddball",
                    image = R.drawable.fish_whitefish
                ),

                Species(
                    caught_flag = false,
                    species_name = "Other",
                    fish_family = "Oddball",
                    image = R.drawable.fish_other
                )
            )


            database.SpeciesDao().insertSpecies(speciesList) // Wrap the FishSpecies object in a list
        }
    }
    companion object {
        private var INSTANCE: SpeciesRepository? = null
        private const val DATABASE_NAME = "species_database"

        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = SpeciesRepository(context)
            }
        }

        fun get(): SpeciesRepository {
            return INSTANCE
                ?: throw IllegalStateException("SpeciesRepository must be initialized")
        }
    }
}
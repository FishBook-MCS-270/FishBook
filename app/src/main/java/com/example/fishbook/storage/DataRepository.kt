package com.example.fishbook.storage

import android.content.Context
import android.util.Log
import androidx.room.Room
import com.example.fishbook.LakeData.Lake
import com.example.fishbook.LakeData.LakeDatabase
import com.example.fishbook.R
import com.example.fishbook.fishdex.Species
import com.example.fishbook.localCatchDetails.LocalCatchDetails
import com.example.fishbook.species_database.LocalDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.*

private const val DATABASE_NAME = "local_database"

//Clean API used for DataAccess -- Creating Databases and executing SQL
class DataRepository private constructor(context: Context) {

    // Local to store Species-Info and Cache-Details
    private val database: LocalDatabase = Room.databaseBuilder(
        context.applicationContext,
        LocalDatabase::class.java,
        DATABASE_NAME
    )
        .createFromAsset(DATABASE_NAME)
        .build()

    private val lakeDatabase: LakeDatabase = Room.databaseBuilder(
        context.applicationContext,
        LakeDatabase::class.java,
        "lake_data_V2"
    )
        .createFromAsset("lake_data.db")
        .build()

    // ~~Caching Functions
    fun getLocalCatchDetails(userId: String): Flow<List<LocalCatchDetails>> {
        Log.d("DataRepository", "Fetching local catch details for user: $userId")
        return database.LocalCatchDetailsDao().getAllCatchDetailsByUser(userId)
    }

    suspend fun insertCatchDetail(localCatchDetail: LocalCatchDetails) {

        database.LocalCatchDetailsDao().insertCatchDetail(localCatchDetail)
    }

    suspend fun deleteCatchDetailById(id: String) {
        database.LocalCatchDetailsDao().deleteCatchDetailById(id)
    }
    suspend fun deleteCatchDetail(localCatchDetail: LocalCatchDetails) {
        database.LocalCatchDetailsDao().deleteCatchDetail(localCatchDetail)
    }

    suspend fun updateCaughtFlag() {
        database.LocalCatchDetailsDao().updateCaughtFlag()
    }
    suspend fun getBestFish(species: String): LocalCatchDetails? {
        return database.LocalCatchDetailsDao().getBestFish(species)
    }
    // ~~Species Functions

    fun getFishSpecies(): Flow<List<Species>> = database.SpeciesDao().getAllSpecies()

    suspend fun getSpecies(id: UUID): Species = database.SpeciesDao().getSpecies(id)

    suspend fun getSpeciesByName(speciesName: String): Species? {
        return database.SpeciesDao().getSpeciesByName(speciesName)
    }

    // ~~LakeData Functions

    fun getAllLakes(): Flow<List<Lake>> = lakeDatabase.lakeDao().getAllLakes()

    fun getCounties(): Flow<List<String>> = lakeDatabase.lakeDao().getCounties()

    fun getLakesByCounty(county: String): List<Lake> {
        return lakeDatabase.lakeDao().getLakesByCounty(county)
    }

    //Might need to re-think this later but it's a simple fix for now.
    suspend fun prepopulateDatabase() {
        if (database.SpeciesDao().getSpeciesCount() == 0) {
            val speciesList = listOf(
                Species(
                    caught_flag = false,
                    species_name = "Bluegill",
                    fish_family = "Sunfish",
                    image = R.drawable.fish_sunbluegill
                ),
                Species(
                    caught_flag = false,
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
                    caught_flag = false,
                    species_name = "Rock Bass",
                    fish_family = "Sunfish",
                    image = R.drawable.fish_rockbass
                ),
                Species(
                    caught_flag = false,
                    species_name = "Black Crappie",
                    fish_family = "Crappie",
                    image = R.drawable.fish_crappiebl
                ),
                Species(
                    caught_flag = false,
                    species_name = "White Crappie",
                    fish_family = "Crappie",
                    image = R.drawable.fish_crappiewh
                ),
                Species(
                    caught_flag = false,
                    species_name = "Largemouth Bass",
                    fish_family = "Bass",
                    image = R.drawable.fish_lmbass
                ),
                Species(
                    caught_flag = false,
                    species_name = "Smallmouth Bass",
                    fish_family = "Bass",
                    image = R.drawable.fish_smbass
                ),
                Species(
                    caught_flag = false,
                    species_name = "Bullhead",
                    fish_family = "Catfish",
                    image = R.drawable.fish_bullheadcat
                ),
                Species(
                    caught_flag = false,
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
                    caught_flag = false,
                    species_name = "Yellow Perch",
                    fish_family = "Perch",
                    image = R.drawable.fish_yellowperch
                ),
                Species(
                    caught_flag = false,
                    species_name = "Walleye",
                    fish_family = "Perch",
                    image = R.drawable.fish_walleye
                ),
                Species(
                    caught_flag = false,
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
                    caught_flag = false,
                    species_name = "Brook Trout",
                    fish_family = "Trout",
                    image = R.drawable.fish_troutbrook
                ),
                Species(
                    caught_flag = false,
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
                    caught_flag = false,
                    species_name = "Longnose Gar",
                    fish_family = "Gar",
                    image = R.drawable.fish_garln
                ),
                Species(
                    caught_flag = false,
                    species_name = "Shortnose Gar",
                    fish_family = "Gar",
                    image = R.drawable.fish_garsn
                ),
                Species(
                    caught_flag = false,
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
                    caught_flag = false,
                    species_name = "Common Carp",
                    fish_family = "Oddball",
                    image = R.drawable.fish_carp
                ),
                Species(
                    caught_flag = false,
                    species_name = "Northern Hog Sucker",
                    fish_family = "Oddball",
                    image = R.drawable.fish_hogsucker
                ),
                Species(
                    caught_flag = false,
                    species_name = "White Bass",
                    fish_family = "Oddball",
                    image = R.drawable.fish_whitebass
                ),
                Species(
                    caught_flag = false,
                    species_name = "Whitefish",
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
        private var INSTANCE: DataRepository? = null
        private const val DATABASE_NAME = "species_database"

        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = DataRepository(context)
            }
        }

        fun get(): DataRepository {
            return INSTANCE
                ?: throw IllegalStateException("DataRepository must be initialized")
        }
    }
}
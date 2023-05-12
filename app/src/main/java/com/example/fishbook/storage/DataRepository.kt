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
                    image = R.drawable.fish_sunbluegill,
                    sci_name = "Lepomis macrochirus",
                    habitat = "Ponds, lakes, streams",
                    bait = "Worms, insects, small jigs"
                ),
                Species(
                    caught_flag = false,
                    species_name = "Green Sunfish",
                    fish_family = "Sunfish",
                    image = R.drawable.fish_sungreen,
                    sci_name = "Lepomis cyanellus",
                    habitat = "Ponds, lakes, slow-moving rivers",
                    bait = "Worms, small jigs"
                ),
                Species(
                    caught_flag = false,
                    species_name = "Pumpkinseed",
                    fish_family = "Sunfish",
                    image = R.drawable.fish_sunpumpkin,
                    sci_name = "Lepomis gibbosus",
                    habitat = "Ponds, lakes, slow-moving rivers",
                    bait = "Worms, insects, small jigs"
                ),
                Species(
                    caught_flag = false,
                    species_name = "Rock Bass",
                    fish_family = "Sunfish",
                    image = R.drawable.fish_rockbass,
                    sci_name = "Ambloplites rupestris",
                    habitat = "Streams, rivers, rocky areas",
                    bait = "Worms, crayfish, small jigs"
                ),
                Species(
                    caught_flag = false,
                    species_name = "Black Crappie",
                    fish_family = "Crappie",
                    image = R.drawable.fish_crappiebl,
                    sci_name = "Pomoxis nigromaculatus",
                    habitat = "Lakes, ponds, reservoirs",
                    bait = "Minnows, jigs, small spinners"
                ),
                Species(
                    caught_flag = false,
                    species_name = "White Crappie",
                    fish_family = "Crappie",
                    image = R.drawable.fish_crappiewh,
                    sci_name = "Pomoxis annularis",
                    habitat = "Lakes, ponds, reservoirs",
                    bait = "Minnows, jigs, small spinners"
                ),
                Species(
                    caught_flag = false,
                    species_name = "Largemouth Bass",
                    fish_family = "Bass",
                    image = R.drawable.fish_lmbass,
                    sci_name = "Micropterus salmoides",
                    habitat = "Lakes, ponds, rivers, streams, submerged vegetation",
                    bait = "Minnows, worms, crayfish, lures"
                ),
                Species(
                    caught_flag = false,
                    species_name = "Smallmouth Bass",
                    fish_family = "Bass",
                    image = R.drawable.fish_smbass,
                    sci_name = "Micropterus dolomieu",
                    habitat = "Clear streams, rocky areas",
                    bait = "Minnows, worms, crayfish, lures"
                ),
                Species(
                    caught_flag = false,
                    species_name = "Bullhead",
                    fish_family = "Catfish",
                    image = R.drawable.fish_bullheadcat,
                    sci_name = "Ameiurus species",
                    habitat = "Ponds, lakes, slow-moving rivers, muddy bottoms",
                    bait = "Worms, cut bait, stink bait"
                ),
                Species(
                    caught_flag = false,
                    species_name = "Channel Catfish",
                    fish_family = "Catfish",
                    image = R.drawable.fish_channelcat,
                    sci_name = "Ictalurus punctatus",
                    habitat = "Lakes, ponds, rivers, streams, deep holes",
                    bait = "Worms, cut bait, stink bait, chicken liver"
                ),
                Species(
                    caught_flag = false,
                    species_name = "Flathead Catfish",
                    fish_family = "Catfish",
                    image = R.drawable.fish_flatheadcat,
                    sci_name = "Pylodictis olivaris",
                    habitat = "Rivers, lakes, reservoirs",
                    bait = "Live bait (small fish, crayfish)"
                ),
                Species(
                    caught_flag = false,
                    species_name = "Yellow Perch",
                    fish_family = "Perch",
                    image = R.drawable.fish_yellowperch,
                    sci_name = "Perca flavescens",
                    habitat = "Lakes, ponds, slow-moving rivers",
                    bait = "Minnows, worms, small jigs"
                ),
                Species(
                    caught_flag = false,
                    species_name = "Walleye",
                    fish_family = "Perch",
                    image = R.drawable.fish_walleye,
                    sci_name = "Sander vitreus",
                    habitat = "Lakes, rivers, reservoirs, rocky areas",
                    bait = "Minnows, nightcrawlers, jigs"
                ),
                Species(
                    caught_flag = false,
                    species_name = "Northern Pike",
                    fish_family = "Pike",
                    image = R.drawable.fish_pike,
                    sci_name = "Esox lucius",
                    habitat = "Lakes, rivers, weedy areas",
                    bait = "Large minnows, spoons, crankbaits"
                ),
                Species(
                    caught_flag = false,
                    species_name = "Muskellunge",
                    fish_family = "Pike",
                    image = R.drawable.fish_musky,
                    sci_name = "Esox masquinongy",
                    habitat = "Clear lakes, weedy areas, medium rivers",
                    bait = "Large minnows, Large spoons, crankbaits"
                ),
                Species(
                    caught_flag = false,
                    species_name = "Brook Trout",
                    fish_family = "Trout",
                    image = R.drawable.fish_troutbrook,
                    sci_name = "Salvelinus fontinalis",
                    habitat = "Cold streams, rivers, mountainous areas",
                    bait = "Small flies, worms, small spinners"
                ),
                Species(
                    caught_flag = false,
                    species_name = "Brown Trout",
                    fish_family = "Trout",
                    image = R.drawable.fish_troutbrown,
                    sci_name = "Salmo trutta",
                    habitat = "Cold streams, rivers, lakes, mountainous areas",
                    bait = "Flies, spinners, worms, small lures"
                ),
                Species(
                    caught_flag = false,
                    species_name = "Rainbow Trout",
                    fish_family = "Trout",
                    image = R.drawable.fish_troutrainbow,
                    sci_name = "Oncorhynchus mykiss",
                    habitat = "Cold streams, rivers, lakes, stocked areas",
                    bait = "Flies, spinners, worms, small lures"
                ),
                Species(
                    caught_flag = false,
                    species_name = "Longnose Gar",
                    fish_family = "Gar",
                    image = R.drawable.fish_garln,
                    sci_name = "Lepisosteus osseus",
                    habitat = "Rivers, lakes, backwaters",
                    bait = "Live minnows, worms, crayfish"
                ),
                Species(
                    caught_flag = false,
                    species_name = "Shortnose Gar",
                    fish_family = "Gar",
                    image = R.drawable.fish_garsn,
                    sci_name = "Lepisosteus platostomus",
                    habitat = "Rivers, lakes, backwaters",
                    bait = "Live minnows, worms, crayfish"
                ),
                Species(
                    caught_flag = false,
                    species_name = "Eelpout",
                    fish_family = "Oddball",
                    image = R.drawable.fish_eelpout,
                    sci_name = "Lota lota",
                    habitat = "Freshwater lakes, rivers, deep rocky areas",
                    bait = "Worms, cut bait"
                ),
                Species(
                    caught_flag = false,
                    species_name = "Bowfin",
                    fish_family = "Oddball",
                    image = R.drawable.fish_bowfin,
                    sci_name = "Amia calva",
                    habitat = "Swamps, slow-moving rivers, backwaters",
                    bait = "Live bait (minnows, worms), crayfish"
                ),
                Species(
                    caught_flag = false,
                    species_name = "Buffalo",
                    fish_family = "Oddball",
                    image = R.drawable.fish_buffalo,
                    sci_name = "Ictiobus spp.",
                    habitat = "Rivers, reservoirs, slow-moving waters",
                    bait = "Dough balls, corn"
                ),
                Species(
                    caught_flag = false,
                    species_name = "Common Carp",
                    fish_family = "Oddball",
                    image = R.drawable.fish_carp,
                    sci_name = "Cyprinus carpio",
                    habitat = "Lakes, ponds, slow-moving rivers, weedy areas",
                    bait = "Bread, corn, dough balls, boilies"
                ),
                Species(
                    caught_flag = false,
                    species_name = "Northern Hog Sucker",
                    fish_family = "Oddball",
                    image = R.drawable.fish_hogsucker,
                    sci_name = "Hypentelium nigricans",
                    habitat = "Clear streams, rivers, rocky or sandy bottoms",
                    bait = "Worms, small crustaceans, aquatic insects"
                ),
                Species(
                    caught_flag = false,
                    species_name = "White Bass",
                    fish_family = "Oddball",
                    image = R.drawable.fish_whitebass,
                    sci_name = "Morone chrysops",
                    habitat = "Lakes, reservoirs, rivers, open water",
                    bait = "Minnows, jigs, spinners"
                ),
                Species(
                    caught_flag = false,
                    species_name = "Whitefish",
                    fish_family = "Oddball",
                    image = R.drawable.fish_whitefish,
                    sci_name = "Coregonus spp.",
                    habitat = "Cold freshwater lakes and rivers",
                    bait = "Insects, worms, small fish, fly patterns"
                ),
                Species(
                    caught_flag = false,
                    species_name = "Other",
                    fish_family = "Oddball",
                    image = R.drawable.fish_other,
                    sci_name = "Other sp.",
                    habitat = "Any body of water",
                    bait = "Any bait you choice"
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
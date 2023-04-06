package com.example.fishbook

import FishAdapter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


/**
 * A simple [Fragment] subclass.
 * Use the [FishDex.newInstance] factory method to
 * create an instance of this fragment.
 */
class FishDex : Fragment() {

    private lateinit var fishAdapter: FishAdapter
    private lateinit var fishList: List<FishSpecies>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_fish_dex, container, false)

        // Initialize the fish species list with two fish instances
        fishList = listOf(
            FishSpecies(
                caught_flag = true,
                species_name = "Bluegill",
                fish_family = "Sunfish",
                image = R.drawable.fish_sunbluegill
            ),
            FishSpecies(
                caught_flag = true,
                species_name = "Green Sunfish",
                fish_family = "Sunfish",
                image = R.drawable.fish_sungreen
            ),
            FishSpecies(
                caught_flag = true,
                species_name = "Pumpkinseed",
                fish_family = "Sunfish",
                image = R.drawable.fish_sunpumpkin
            ),
            FishSpecies(
                caught_flag = true,
                species_name = "Rock Bass",
                fish_family = "Sunfish",
                image = R.drawable.fish_rockbass
            ),
            FishSpecies(
                caught_flag = true,
                species_name = "Black Crappie",
                fish_family = "Crappie",
                image = R.drawable.fish_crappiebl
            ),
            FishSpecies(
                caught_flag = true,
                species_name = "White Crappie",
                fish_family = "Crappie",
                image = R.drawable.fish_crappiewh
            ),
            FishSpecies(
                caught_flag = true,
                species_name = "Largemouth Bass",
                fish_family = "Bass",
                image = R.drawable.fish_lmbass
            ),
            FishSpecies(
                caught_flag = true,
                species_name = "Smallmouth Bass",
                fish_family = "Bass",
                image = R.drawable.fish_smbass
            ),


            FishSpecies(
                caught_flag = true,
                species_name = "Bullhead",
                fish_family = "Catfish",
                image = R.drawable.fish_bullheadcat
            ),
            FishSpecies(
                caught_flag = true,
                species_name = "Channel Catfish",
                fish_family = "Catfish",
                image = R.drawable.fish_channelcat
            ),
            FishSpecies(
                caught_flag = true,
                species_name = "Flathead Catfish",
                fish_family = "Catfish",
                image = R.drawable.fish_flatheadcat
            ),
            FishSpecies(
                caught_flag = true,
                species_name = "Yellow Perch",
                fish_family = "Perch",
                image = R.drawable.fish_yellowperch
            ),
            FishSpecies(
                caught_flag = true,
                species_name = "Walleye",
                fish_family = "Perch",
                image = R.drawable.fish_walleye
            ),
            FishSpecies(
                caught_flag = true,
                species_name = "Northern Pike",
                fish_family = "Pike",
                image = R.drawable.fish_pike
            ),
            FishSpecies(
                caught_flag = true,
                species_name = "Muskellunge",
                fish_family = "Pike",
                image = R.drawable.fish_musky
            ),
            FishSpecies(
                caught_flag = true,
                species_name = "Brook Trout",
                fish_family = "Trout",
                image = R.drawable.fish_troutbrook
            ),
            FishSpecies(
                caught_flag = true,
                species_name = "Brown Trout",
                fish_family = "Trout",
                image = R.drawable.fish_troutbrown
            ),
            FishSpecies(
                caught_flag = true,
                species_name = "Rainbow Trout",
                fish_family = "Trout",
                image = R.drawable.fish_troutrainbow
            ),
            FishSpecies(
                caught_flag = true,
                species_name = "Longnose Gar",
                fish_family = "Gar",
                image = R.drawable.fish_garln
            ),
            FishSpecies(
                caught_flag = true,
                species_name = "Shortnose Gar",
                fish_family = "Gar",
                image = R.drawable.fish_garsn
            ),
            FishSpecies(
                caught_flag = true,
                species_name = "Eelpout",
                fish_family = "Oddball",
                image = R.drawable.fish_eelpout
            ),
            FishSpecies(
                caught_flag = true,
                species_name = "Bowfin",
                fish_family = "Oddball",
                image = R.drawable.fish_bowfin
            ),
            FishSpecies(
                caught_flag = true,
                species_name = "Buffalo",
                fish_family = "Oddball",
                image = R.drawable.fish_buffalo
            ),
            FishSpecies(
                caught_flag = true,
                species_name = "Common Carp",
                fish_family = "Oddball",
                image = R.drawable.fish_carp
            ),
            FishSpecies(
                caught_flag = true,
                species_name = "Northern Hogsucker",
                fish_family = "Oddball",
                image = R.drawable.fish_hogsucker
            ),
            FishSpecies(
                caught_flag = true,
                species_name = "White Bass",
                fish_family = "Oddball",
                image = R.drawable.fish_whitebass
            ),
            FishSpecies(
                caught_flag = true,
                species_name = "White Fish",
                fish_family = "Oddball",
                image = R.drawable.fish_whitefish
            ),

            FishSpecies(
                caught_flag = true,
                species_name = "Other",
                fish_family = "Oddball",
                image = R.drawable.fish_other
            )
        )

        fishAdapter = FishAdapter(fishList)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.apply {
            //assures that the layout is filled
            layoutManager = LinearLayoutManager(requireContext())
            adapter = fishAdapter
        }

        return view
    }
}

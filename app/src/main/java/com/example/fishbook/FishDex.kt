package com.example.fishbook

import com.example.fishbook.FishAdapter
import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController



class FishDex : Fragment() {

    private lateinit var fishAdapter: FishAdapter
    private val fishDexViewModel: FishDexViewModel by viewModels {
        ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application as Application)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_fish_dex, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("FishDex", "onViewCreated called")

        // Set up the RecyclerView with an empty adapter
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        fishAdapter = FishAdapter(emptyList()) { fishId ->
            val action = FishDexDirections.actionFishDexToSpecies(fishId)
            findNavController().navigate(action)
        }
        recyclerView.adapter = fishAdapter

        fishDexViewModel.fishSpeciesList.observe(viewLifecycleOwner) { fishList ->
            // Update the fishAdapter with the new fishList
            fishAdapter.fishList = fishList
            fishAdapter.notifyDataSetChanged()
        }
    }}
package com.example.fishbook.fishdex

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.example.fishbook.LakeData.Lake
import com.example.fishbook.MainActivity
import com.example.fishbook.R
import com.example.fishbook.databinding.FragmentSpeciesDetailBinding
import kotlinx.coroutines.launch

class SpeciesDetailFragment : Fragment() {
    //Sourced via crimedetail fragment
    private var _binding: FragmentSpeciesDetailBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    private val args: SpeciesDetailFragmentArgs by navArgs()

    private val speciesDetailViewModel: SpeciesDetailViewModel by viewModels(){
       SpeciesDetailViewModelFactory(args.fishSpeciesId)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSpeciesDetailBinding.inflate(inflater, container, false)
        val mainActivity = requireActivity() as MainActivity

        binding.findSpeciesButton.setOnClickListener {
            lifecycleScope.launch {
                val fishSpecies = speciesDetailViewModel.species.value?.species_name
                Log.d("Species", "$fishSpecies")
                if (fishSpecies != null) {
                    mainActivity.findNearestLakes(10, fishSpecies =  fishSpecies.lowercase()).let { nearestLakes ->
                        showNearestLakesDialog(nearestLakes)
                    }
                }
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                speciesDetailViewModel.species.collect { fishSpecies ->
                    fishSpecies?.let { updateUi(it) }
                }
            }
        }
    }

    private fun showNearestLakesDialog(nearestLakes: List<Pair<Lake, Double>>) {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.dialog_nearest_lakes, null)

        val listView = view.findViewById<ListView>(R.id.nearest_lakes_list_view)
        val lakeNames = nearestLakes.map { "${it.first.lakeName}, ${it.second} miles" }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, lakeNames)
        listView.adapter = adapter

        val dialog = builder.setView(view)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        dialog.show()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun updateUi(fish: Species) {
        binding.apply {
            if (fish.caught_flag) {
                fishName.text = fish.species_name
                fishFamily.text = fish.fish_family
                fishImage.setImageResource(fish.image)
                fishImage.clearColorFilter()
            } else {
                fishName.text = fish.species_name
                fishFamily.text = "???"
                fishImage.setImageResource(fish.image)
                fishImage.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP)
            }
        }
    }
}

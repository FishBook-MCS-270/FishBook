package com.example.fishbook.fishdex

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.example.fishbook.databinding.FragmentSpeciesDetailBinding
import kotlinx.coroutines.launch

class SpeciesDetailFragment : Fragment() {

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


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun updateUi(fish: Species) {
        binding.apply {
            fishName.text = fish.species_name
            fishFamily.text = fish.fish_family
            fishImage.setImageResource(fish.image)
        }
    }
}

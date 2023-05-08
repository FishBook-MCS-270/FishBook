package com.example.fishbook.gallery

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.fishbook.R

import com.example.fishbook.databinding.FragmentGalleryBinding
import kotlinx.coroutines.launch



class Gallery : Fragment() {
    private var _binding: FragmentGalleryBinding? = null
    private val binding get() = _binding!!

    private val galleryViewModel: GalleryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        galleryViewModel.newSpeciesEvent.observe(viewLifecycleOwner) { species ->
            if (species != null) {
                showNewSpeciesDialog(requireContext(), species.species_name, species.image)
            }
        }
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        retainInstance = true

        // create adapter
        val galleryAdapter = GalleryAdapter(requireContext(), emptyList()) { catchDetail ->
            findNavController().navigate(GalleryDirections.showRecord(catchDetail))
        }
        binding.gridView.adapter = galleryAdapter
        galleryViewModel.fetchCatchDetails()

        //update from firestore automatically
        galleryViewModel.CatchDetails.observe(viewLifecycleOwner) { catchDetails ->
            galleryAdapter.updateData(catchDetails)
        }

        binding.addRecord.setOnClickListener {
            findNavController().navigate(R.id.addRecord)
        }

        //used for newSpecies
        galleryViewModel.newSpeciesEvent.observe(viewLifecycleOwner) { newSpecies ->
            newSpecies?.let {
                val imageResource = it.image
                showNewSpeciesDialog(requireContext(), it.species_name, imageResource)
            }
        }
    }

    fun showNewSpeciesDialog(context: Context, species: String, imageResource: Int) {
        val builder = AlertDialog.Builder(context)
        val inflater = (context as Activity).layoutInflater
        val view = inflater.inflate(R.layout.dialog_new_species, null)

        view.findViewById<TextView>(R.id.species_name).text = species
        view.findViewById<ImageView>(R.id.species_image).setImageResource(imageResource)

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
}

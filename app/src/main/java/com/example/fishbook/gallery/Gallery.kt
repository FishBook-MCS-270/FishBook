package com.example.fishbook.gallery

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.fishbook.R
import com.example.fishbook.databinding.FragmentGalleryBinding
import nl.dionsegijn.konfetti.core.Angle
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.Spread
import nl.dionsegijn.konfetti.core.emitter.Emitter
import nl.dionsegijn.konfetti.core.models.Shape
import nl.dionsegijn.konfetti.core.models.Size
import java.util.concurrent.TimeUnit


class Gallery : Fragment() {
    private var _binding: FragmentGalleryBinding? = null
    private val binding get() = _binding!!
    private val galleryViewModel: GalleryViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGalleryBinding.inflate(inflater, container, false)

        return binding.root

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        retainInstance = true
        Log.d("Gallery", "creat")

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
        //observe newSpecies event
        galleryViewModel.newSpeciesEvent.observe(viewLifecycleOwner) { newSpecies ->
            if (newSpecies != null) {
                showConfetti()
                showNewSpeciesDialog(newSpecies.species_name, newSpecies.image)
                galleryViewModel._newSpeciesEvent.value = null
            }
        }

    }

    fun showNewSpeciesDialog(species: String, imageResource: Int) {
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

    //https://github.com/DanielMartinus/Konfetti
    //Party object used to specify confetti
    private fun showConfetti() {
        val party = Party(
            speed = 0f,
            maxSpeed = 15f,
            damping = 0.9f,
            angle = Angle.BOTTOM,
            spread = Spread.ROUND,
            colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def),
            emitter = Emitter(duration = 3, TimeUnit.SECONDS).perSecond(100),
            position = Position.Relative(0.0, 0.0).between(Position.Relative(1.0, 0.0))
        )
        val viewKonfetti = binding.konfettiView
        viewKonfetti.start(party)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

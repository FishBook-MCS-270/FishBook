package com.example.fishbook.gallery

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

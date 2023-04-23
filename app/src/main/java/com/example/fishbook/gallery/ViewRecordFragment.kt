package com.example.fishbook.gallery

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.example.fishbook.databinding.FragmentViewRecordBinding
import com.squareup.picasso.Picasso
import androidx.navigation.fragment.navArgs
import com.example.fishbook.gallery.ViewRecordFragmentArgs
import com.example.fishbook.record.CatchDetails
import androidx.navigation.fragment.findNavController


class ViewRecordFragment : Fragment() {
    private var _binding: FragmentViewRecordBinding? = null
    private val binding get() = _binding!!
    private val args: ViewRecordFragmentArgs by navArgs()
    private val galleryViewModel: GalleryViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentViewRecordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val catchDetail = args.catchDetail
        updateUi(catchDetail)
        galleryViewModel.updatedCatchDetails.observe(viewLifecycleOwner) { updatedCatchDetail ->
            if (updatedCatchDetail.id == args.catchDetail.id) {
                updateUi(updatedCatchDetail)
            }
        }

        binding.buttonEdit.setOnClickListener {
            val action = ViewRecordFragmentDirections.editRecord(catchDetail)
            findNavController().navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun updateUi(catchDetail: CatchDetails) {
        binding.apply {
            Picasso.get().load(catchDetail.remoteUri).into(largeImage)
           tvSpecies.text = catchDetail.species
            tvCounty.text = catchDetail.county
            tvLake.text = catchDetail.lake
            tvLure.text = catchDetail.lure

            tvWeight.text = catchDetail.weight.toString()
            tvLength.text = catchDetail.length.toString()

        }
    }
}

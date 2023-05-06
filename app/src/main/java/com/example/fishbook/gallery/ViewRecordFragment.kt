package com.example.fishbook.gallery

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.example.fishbook.databinding.FragmentViewRecordBinding
import com.squareup.picasso.Picasso
import androidx.navigation.fragment.navArgs
import com.example.fishbook.gallery.ViewRecordFragmentArgs
import com.example.fishbook.record.CatchDetails
import androidx.navigation.fragment.findNavController
import com.example.fishbook.fishdex.DataRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class ViewRecordFragment : Fragment() {
    private var _binding: FragmentViewRecordBinding? = null
    private val binding get() = _binding!!
    private val args: ViewRecordFragmentArgs by navArgs()
    private val galleryViewModel: GalleryViewModel by activityViewModels()
    private val dataRepository = DataRepository.get()

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

        binding.deleteButton.setOnClickListener{
            val db = FirebaseFirestore.getInstance()
            // gets user id for current user
            FirebaseAuth.getInstance().currentUser?.uid?.let { userId ->
                db.collection("users")
                    .document(userId)
                    .collection("catchDetails")
                    .document(catchDetail.id) // reference to catch detail id
                    .delete() // deletes current catch detail record

                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Record deleted", Toast.LENGTH_SHORT).show()
                        // goes back to gallery when record is deleted
                        galleryViewModel.deleteCatchDetail(catchDetail)

                        findNavController().popBackStack()
                    }
                    .addOnFailureListener {
                        // error deleting document
                        Toast.makeText(requireContext(), "Error deleting", Toast.LENGTH_SHORT).show()

                    }
            }
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
            tvLatitude.text = catchDetail.latitude
            tvLongitude.text = catchDetail.longitude
            tvWeight.text = catchDetail.weight.toString()
            tvLength.text = catchDetail.length.toString()

        }
    }
}

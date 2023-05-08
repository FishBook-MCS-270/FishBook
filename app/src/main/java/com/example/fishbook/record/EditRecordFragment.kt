package com.example.fishbook.record
import android.R
import android.content.ContentValues
import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.fishbook.databinding.FragmentAddRecordBinding
import com.example.fishbook.record.CatchDetails
import androidx.navigation.fragment.findNavController
import com.example.fishbook.databinding.FragmentEditRecordBinding
import com.example.fishbook.fishdex.Species
import com.example.fishbook.gallery.GalleryViewModel


class EditRecordFragment : Fragment() {

    private lateinit var binding: FragmentEditRecordBinding
    private val args: EditRecordFragmentArgs by navArgs()
    private val galleryViewModel: GalleryViewModel by activityViewModels()
    private val addRecordViewModel: AddRecordViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditRecordBinding.inflate(inflater, container, false)

        addRecordViewModel.allSpecies.observe(viewLifecycleOwner) { speciesList ->
            setupSpeciesAutoCompleteTextView(speciesList)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val catchDetail = args.catchDetail
        populateFields(catchDetail)

        binding.submitButton.text = "Update"
        binding.submitButton.setOnClickListener {
            updateCatchDetails(catchDetail)
        }
    }

    private fun populateFields(catchDetail: CatchDetails) {
        binding.apply {
            fishImage.setImageURI(Uri.parse(catchDetail.localUri))
            speciesEditText.setText(catchDetail.species)
            lureEditText.setText(catchDetail.lure)
            lengthEditText.setText(catchDetail.length.toString())
            weightEditText.setText(catchDetail.weight.toString())

        }
    }
    private fun setupSpeciesAutoCompleteTextView(speciesList: List<Species>) {
        val speciesNames = speciesList.map { it.species_name }
        val adapter = ArrayAdapter(requireContext(), R.layout.simple_dropdown_item_1line, speciesNames)
        binding.speciesEditText.setAdapter(adapter)
    }
    private fun updateCatchDetails(catchDetail: CatchDetails) {
        Log.d("EditRecordFragment", "Updating catch details")

        val updatedCatchDetails = catchDetail.copy(
            species = binding.speciesEditText.text.toString(),
            lure = binding.lureEditText.text.toString(),
            length = binding.lengthEditText.text.toString(),
            weight = binding.weightEditText.text.toString(),

        )

        val db = FirebaseFirestore.getInstance()
        FirebaseAuth.getInstance().currentUser?.uid?.let { userId ->
            db.collection("users")
                .document(userId)
                .collection("catchDetails")
                .document(catchDetail.id)
                .set(updatedCatchDetails)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Record updated successfully", Toast.LENGTH_SHORT).show()
                    galleryViewModel.updateCatchDetails(updatedCatchDetails)
                    Log.d("EditRecordFragment", "Record updated successfully in Firestore")

                    findNavController().popBackStack()
                    findNavController().popBackStack()

                }
                .addOnFailureListener {
                    // Error updating document
                    Toast.makeText(requireContext(), "Error Updating", Toast.LENGTH_SHORT).show()

                }
        }
    }
}

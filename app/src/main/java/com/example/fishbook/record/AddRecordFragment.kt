package com.example.fishbook.record
import androidx.navigation.fragment.findNavController
import androidx.core.widget.doAfterTextChanged

import android.app.Activity.RESULT_OK
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.fishbook.R
import com.example.fishbook.databinding.FragmentAddRecordBinding
import com.example.fishbook.gallery.GalleryViewModel
import com.example.fishbook.localCatchDetails.LocalCatchDetails
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import androidx.fragment.app.viewModels
import com.example.fishbook.LakeData.Lake
import com.example.fishbook.fishdex.Species
import androidx.core.widget.doOnTextChanged


class AddRecordFragment : Fragment() {

    private lateinit var binding: FragmentAddRecordBinding
    private val galleryViewModel: GalleryViewModel by activityViewModels()
    private val addRecordViewModel: AddRecordViewModel by viewModels()

    private var photoName: String? = null
    private lateinit var photoFile: File
    private lateinit var ImageUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        takePhotoLauncher = registerForActivityResult(
            ActivityResultContracts.TakePicture()
        ) { didTakePhoto: Boolean ->
            if (didTakePhoto && photoName != null) {
                val photoUri = FileProvider.getUriForFile(
                    requireContext(),
                    "com.example.fishbook.fileprovider",
                    photoFile
                )
                ImageUri = photoUri // assign photoUri to ImageUri
                binding.fishImage.setImageURI(photoUri)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentAddRecordBinding.inflate(layoutInflater, container, false)
        addRecordViewModel.fetchAllSpecies(requireContext())
        addRecordViewModel.allSpecies.observe(viewLifecycleOwner) { speciesList ->
            setupSpeciesAutoCompleteTextView(speciesList)
        }

        binding.cameraButton.setOnClickListener {
            photoName = "IMG_${Date()}.JPG"
            photoFile = File(
                requireContext().applicationContext.filesDir,
                photoName
            )
            val photoUri = FileProvider.getUriForFile(
                requireContext(),
                "com.example.fishbook.fileprovider",
                photoFile
            )
            takePhotoLauncher.launch(photoUri)
        }

        binding.submitButton.text = getString(R.string.upload)

        binding.submitButton.setOnClickListener{
            uploadImage()
        }

        binding.selectImageButton.setOnClickListener{
            selectImage()
        }

        addRecordViewModel.fetchCounties(requireContext())
        //used for the autofill, SQL
        addRecordViewModel.countyList.observe(viewLifecycleOwner) { countyList ->
            setupCountyAutoCompleteTextView(countyList)
        }

        return binding.root
    }

    //~~AutoFill Functions
    private fun setupSpeciesAutoCompleteTextView(speciesList: List<Species>) {
        val speciesNames = speciesList.map { it.species_name }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, speciesNames)
        binding.speciesEditText.setAdapter(adapter)
    }

    private fun setupCountyAutoCompleteTextView(countyList: List<String>) {
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, countyList)
        binding.countyEditText.setAdapter(adapter)

        binding.countyEditText.doOnTextChanged { text, _, _, _ ->
            if (text != null) {
                val selectedCounty = text.toString()
                Log.d("AddRecordFragment", "County: $selectedCounty")

                // Clears when the county is changed
                binding.lakeEditText.text = null
                binding.latEditText.text = null
                binding.longEditText.text = null

                addRecordViewModel.fetchLakesByCounty(selectedCounty)
                addRecordViewModel.lakeList.observe(viewLifecycleOwner) { lakeDataList ->
                    Log.d(
                        "AddRecordFragment",
                        "Fetched ${lakeDataList.size} Lakes"
                    )

                    //CHANGE LATER -- Grabs first GPS Value for lake in County
                    if (lakeDataList.isNotEmpty()) {
                        binding.latEditText.setText(lakeDataList[0].gps_lat.toString())
                        binding.longEditText.setText(lakeDataList[0].gps_long.toString())
                        setupLakeNameAutoCompleteTextView(lakeDataList)

                    }
                    else {
                        setupLakeNameAutoCompleteTextView(lakeDataList)
                    }
                }
            }
        }
    }

    private fun setupLakeNameAutoCompleteTextView(lakeDataList: List<Lake>) {
        val lakeNames = lakeDataList.map { it.lakeName }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, lakeNames)
        binding.lakeEditText.setAdapter(adapter)

        binding.lakeEditText.doOnTextChanged { text, _, _, _ ->
            if (text != null) {
                val selectedLake = text.toString()
                val selectedLakeData = lakeDataList.firstOrNull { it.lakeName == selectedLake }

                if (selectedLakeData != null) {
                    // Update the GPS Data
                    Log.d("AddRecordFragment", "Lake: ${selectedLakeData.lakeName} Lat: ${selectedLakeData.gps_lat}")
                    binding.latEditText.setText(selectedLakeData.gps_lat.toString())
                    binding.longEditText.setText(selectedLakeData.gps_long.toString())
                }
            }
        }
    }


    private var takePhotoLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { didTakePhoto: Boolean ->
        if (didTakePhoto && photoName != null) {
            binding.fishImage.setImageURI(
                FileProvider.getUriForFile(
                requireContext(),
                "com.example.fishbook.fileprovider",
                photoFile
            ))
        }
    }

    private fun uploadImage() {
        val progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("Uploading File...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
        val now = Date()
        val placeholderId = UUID.randomUUID().toString()
        val fileName = formatter.format(now)
        val storageReference = FirebaseStorage.getInstance().getReference("images/$fileName")

        storageReference.putFile(ImageUri).addOnSuccessListener {
            // Get the download URL of the uploaded image
            storageReference.downloadUrl.addOnSuccessListener { uri ->
                val remoteUri = uri.toString()
                binding.fishImage.setImageURI(null)
                Toast.makeText(requireContext(), "Successfully uploaded", Toast.LENGTH_SHORT).show()
                if (progressDialog.isShowing) progressDialog.dismiss()
                // Store info in CatchDetails object
                val catchDetails = CatchDetails(
                    id = placeholderId,
                    species = binding.speciesEditText.text.toString(),
                    lake = binding.lakeEditText.text.toString(),
                    lure = binding.lureEditText.text.toString(),
                    length = binding.lengthEditText.text.toString(),
                    weight = binding.weightEditText.text.toString(),
                    county = binding.countyEditText.text.toString(),
                    latitude = binding.latEditText.text.toString(),
                    longitude = binding.longEditText.text.toString(),
//                    time = binding.timeEditText.text.toString(),
//                    location = binding.locationEditText.text.toString(),
                    localUri = ImageUri.toString(),
                    remoteUri = remoteUri
                )
                uploadCatchDetails(catchDetails)
            }
        }.addOnFailureListener {
            if (progressDialog.isShowing) progressDialog.dismiss()
            Toast.makeText(requireContext(), "Failed", Toast.LENGTH_SHORT).show()
        }
    }

    // register an activity result launcher to handle the image selection intent
    private val selectImageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val imageUri = result.data?.data
            if (imageUri != null) {
                ImageUri = imageUri
                binding.fishImage.setImageURI(imageUri)
            }
        }
    }

    private fun selectImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        // launch the activity result launcher to handle the intent
        selectImageLauncher.launch(intent)
    }

    // uploads catchDetails to firebase
    private fun uploadCatchDetails(catchDetails: CatchDetails) {
        val db = Firebase.firestore
        // adds to catchDetails collection
        FirebaseAuth.getInstance().currentUser?.uid?.let { userId ->
            db.collection("users")
                .document(userId)
                .collection("catchDetails")
                .add(catchDetails)
                .addOnSuccessListener {documentReference ->
                    Log.i(ContentValues.TAG, "Successfully added catch details")

                    val generatedId = documentReference.id

                    // Update the CatchDetails object with the new ID
                    val updatedCatchDetails = catchDetails.copy(id = generatedId)

                    // Update the document in Firestore
                    documentReference.update("id", generatedId)
                    val localCatchDetails = LocalCatchDetails(
                        id = updatedCatchDetails.id,
                        userId = userId,
                        species = updatedCatchDetails.species,
                        lake = updatedCatchDetails.lake,
                        length = updatedCatchDetails.length,
                        weight = updatedCatchDetails.weight,
                        county = updatedCatchDetails.county,
                        lure = updatedCatchDetails.lure,
                        latitude = updatedCatchDetails.latitude,
                        longitude = updatedCatchDetails.longitude,
                        remoteUri = updatedCatchDetails.remoteUri,
                        localUri = updatedCatchDetails.localUri
                    )
                    galleryViewModel.updateCatchDetails(updatedCatchDetails)

                    findNavController().popBackStack()
                    // clears text boxes if uploaded successfully
                    binding.speciesEditText.text.clear()
                    binding.lakeEditText.text.clear()
                    binding.lureEditText.text.clear()
                    binding.lengthEditText.text.clear()
                    binding.weightEditText.text.clear()
                    binding.countyEditText.text.clear()
                    binding.latEditText.text.clear()
                    binding.longEditText.text.clear()
//                    binding.timeEditText.text.clear()
//                    binding.locationEditText.text.clear()
                }
                .addOnFailureListener {
                    Log.e(ContentValues.TAG, "Error adding document")
                }
        }
    }
}
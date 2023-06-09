package com.example.fishbook.record
import androidx.navigation.fragment.findNavController

import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.fishbook.R
import com.example.fishbook.databinding.FragmentAddRecordBinding
import com.example.fishbook.gallery.GalleryViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import com.example.fishbook.LakeData.Lake
import com.example.fishbook.fishdex.Species
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.example.fishbook.MainActivity
import kotlinx.coroutines.launch


class AddRecordFragment : Fragment() {

    private lateinit var binding: FragmentAddRecordBinding
    private val galleryViewModel: GalleryViewModel by activityViewModels()

    private val addRecordViewModel: AddRecordViewModel by activityViewModels()
    private var dialogFlag = false //fixes bug to use user-location for gps,

    private var photoName: String? = null
    private lateinit var photoFile: File
    private var ImageUri: Uri? = null

    // bundle to save latitude and longitude info
    private val gpsbundle = Bundle()

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
                addRecordViewModel.catchUri = photoUri // update catchUri in the ViewModel
                binding.fishImage.setImageURI(addRecordViewModel.catchUri) // use catchUri to update fishImage
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentAddRecordBinding.inflate(layoutInflater, container, false)
        addRecordViewModel.fetchAllSpecies(requireContext())
        addRecordViewModel.allSpecies.observe(viewLifecycleOwner) { speciesList ->
            setupSpeciesAutoCompleteTextView(speciesList)
        }

        binding.cameraButton.setOnClickListener {
            photoName = "IMG_${Date()}.JPG"
            photoFile = File(
                requireContext().applicationContext.filesDir,
                photoName.toString()
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
        val mainActivity = requireActivity() as MainActivity

        binding.useLocationButton.setOnClickListener {
            lifecycleScope.launch {
                mainActivity.findNearestLakes().let { nearestLakes ->
                    showNearestLakesDialog(nearestLakes)
                }
            }
        }
        addRecordViewModel.fetchCounties(requireContext())
        //used for the autofill, SQL
        addRecordViewModel.countyList.observe(viewLifecycleOwner) { countyList ->
            setupCountyAutoCompleteTextView(countyList)
        }

        binding.locationButton.setOnClickListener{
            // saves latitude and longitude in a bundle to allow use in different fragment
            binding.latEditText.text.toString().toDoubleOrNull()
                ?.let { it1 -> gpsbundle.putDouble("latitude", it1) }
            binding.longEditText.text.toString().toDoubleOrNull()
                ?.let { it1 -> gpsbundle.putDouble("longitude", it1) }
            findNavController().navigate(R.id.setLoc, gpsbundle)
        }

        // retrieves details from view model and sets text
        //Log.i("details", "uri: $savedUri")
        if (ImageUri != addRecordViewModel.catchUri) {
            addRecordViewModel.catchUri = ImageUri
        }
        binding.speciesEditText.setText(addRecordViewModel.catchSpecies)
        binding.lakeEditText.setText(addRecordViewModel.catchLake)
        binding.countyEditText.setText(addRecordViewModel.catchCounty)
        binding.lureEditText.setText(addRecordViewModel.catchLure)
        binding.lengthEditText.setText(addRecordViewModel.catchLength)
        binding.weightEditText.setText(addRecordViewModel.catchWeight)

        return binding.root
    }
    override fun onPause() {
        super.onPause()
        // stores catch details to view model
        addRecordViewModel.catchUri = ImageUri
        addRecordViewModel.catchSpecies = binding.speciesEditText.text?.toString()
        addRecordViewModel.catchLake = binding.lakeEditText.text?.toString()
        addRecordViewModel.catchCounty = binding.countyEditText.text?.toString()
        addRecordViewModel.catchLure = binding.lureEditText.text?.toString()
        addRecordViewModel.catchLength = binding.lengthEditText.text?.toString()
        addRecordViewModel.catchWeight = binding.weightEditText.text?.toString()
    }

    override fun onResume() {
        super.onResume()

        setFragmentResultListener("requestKey") { _, bundle ->
            val locationBundle = bundle.getBundle("bundleKey")
            val markerLatitude = locationBundle?.getString("markerLatitude")
            val markerLongitude = locationBundle?.getString("markerLongitude")
            binding.latEditText.setText(markerLatitude)
            binding.longEditText.setText(markerLongitude)
        }

        Log.d("MyFragment", "on resume view uri: ${addRecordViewModel.catchUri}")
        binding.fishImage.setImageURI(addRecordViewModel.catchUri)
    }

    override fun onDestroyView() {
        super.onDestroyView()
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

        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedLake = nearestLakes[position]
            handleLakeItemClick(selectedLake, dialog)
        }

        dialog.show()
    }

    private fun handleLakeItemClick(selectedLake: Pair<Lake, Double>, dialog: AlertDialog) {
        dialogFlag = true
        //clearest after each click
        binding.countyEditText.text = null
        binding.lakeEditText.text = null
        binding.latEditText.text = null
        binding.longEditText.text = null
        binding.countyEditText.setText(selectedLake.first.county)

        //need to nullify the GPS Cords to fix bug of setting county's gps
        binding.latEditText.text = null
        binding.longEditText.text = null
        binding.lakeEditText.setText(selectedLake.first.lakeName)
        binding.latEditText.setText(selectedLake.first.gps_lat.toString())
        binding.longEditText.setText(selectedLake.first.gps_long.toString())
        dialogFlag = false

        dialog.dismiss()

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
            if (text != null && !dialogFlag) {
                val selectedCounty = text.toString()
                Log.d("AddRecordFragment", "County: $selectedCounty")

                // Clears when the county is changed
                binding.lakeEditText.text = null
                binding.latEditText.text = null
                binding.longEditText.text = null

                addRecordViewModel.fetchLakesByCounty(selectedCounty)
                addRecordViewModel.lakeList.observe(viewLifecycleOwner) { lakeDataList ->

                    if (lakeDataList.isNotEmpty() && !dialogFlag) {
                        binding.latEditText.setText(lakeDataList[0].gps_lat.toString())
                        binding.longEditText.setText(lakeDataList[0].gps_long.toString())
                        setupLakeNameAutoCompleteTextView(lakeDataList)

                    }
                    else {
                        setupLakeNameAutoCompleteTextView(lakeDataList)
                    }
                }
            }
            //dialogFlag = false //TESTING
        }
    }

    private fun setupLakeNameAutoCompleteTextView(lakeDataList: List<Lake>) {
        val lakeNames = lakeDataList.map { it.lakeName }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, lakeNames)
        binding.lakeEditText.setAdapter(adapter)

        binding.lakeEditText.doOnTextChanged { text, _, _, _ ->
            if (text != null && !dialogFlag) {
                binding.latEditText.text = null
                binding.longEditText.text = null

                val selectedLake = text.toString()
                val selectedLakeData = lakeDataList.firstOrNull { it.lakeName == selectedLake } //allows for non-lakes to be put in

                if (selectedLakeData != null) {
                    Log.d("AddRecordFragment", "Lake: ${selectedLakeData.lakeName} Lat: ${selectedLakeData.gps_lat}")
                    binding.latEditText.setText(selectedLakeData.gps_lat.toString())
                    binding.longEditText.setText(selectedLakeData.gps_long.toString())
                }
            }
            //dialogFlag = false //TESTING
        }
    }


    private var takePhotoLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { didTakePhoto: Boolean ->
        if (didTakePhoto && photoName != null) {
            val photoUri = FileProvider.getUriForFile(
                requireContext(),
                "com.example.fishbook.fileprovider",
                photoFile
            )
            ImageUri = photoUri // assign photoUri to ImageUri
            addRecordViewModel.catchUri = photoUri // update catchUri in the ViewModel
            binding.fishImage.setImageURI(addRecordViewModel.catchUri) // use catchUri to update fishImage
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

        storageReference.putFile(ImageUri!!).addOnSuccessListener {
            // Get the download URL of the uploaded image
            storageReference.downloadUrl.addOnSuccessListener { uri ->
                val remoteUri = uri.toString()
                binding.fishImage.setImageURI(null)
                //Toast.makeText(requireContext(), "Successfully uploaded", Toast.LENGTH_SHORT).show()
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
                addRecordViewModel.catchUri = imageUri // update catchUri in the ViewModel
                binding.fishImage.setImageURI(addRecordViewModel.catchUri) // use catchUri to update fishImage
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
                    Log.i(TAG, "Successfully added catch details")

                    val generatedId = documentReference.id

                    // Update the CatchDetails object with the new ID
                    val updatedCatchDetails = catchDetails.copy(id = generatedId)

                    // Update the document in Firestore
                    documentReference.update("id", generatedId)
                    galleryViewModel.viewModelScope.launch {
                        val isNewSpecies = galleryViewModel.checkForNewSpecies(catchDetails.species)
                        if (isNewSpecies) {
                            Log.i("AddRecordFragment", "isNewSpecies")

                            galleryViewModel._newSpeciesFlag.postValue(true)
                        }
                        else {
                            Log.i("AddRecordFragment", "isNOTNewSpecies")

                            galleryViewModel._newSpeciesFlag.postValue(false)
                        }

                    }
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
                }
                .addOnFailureListener {
                    Log.e(TAG, "Error adding document")
                }
        }
    }
}
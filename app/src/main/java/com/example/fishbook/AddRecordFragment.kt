package com.example.fishbook

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
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.example.fishbook.databinding.FragmentAddRecordBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class AddRecordFragment : Fragment() {

    private lateinit var binding: FragmentAddRecordBinding

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

        binding.uploadButton.setOnClickListener{
            uploadImage()
        }

        binding.selectImageButton.setOnClickListener{
            selectImage()
        }
        return binding.root
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
                    species = binding.speciesEditText.text.toString(),
                    lake = binding.lakeEditText.text.toString(),
                    lure = binding.lureEditText.text.toString(),
                    length = binding.lengthEditText.text.toString().toFloat(),
                    weight = binding.weightEditText.text.toString().toFloat(),
                    county = binding.countyEditText.text.toString(),
                    time = binding.timeEditText.text.toString(),
                    location = binding.locationEditText.text.toString(),
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
        db.collection("catchDetails")
            .add(catchDetails)
            .addOnSuccessListener {
                Log.i(ContentValues.TAG, "Successfully added catch details")
                // clears text boxes if uploaded successfully
                binding.speciesEditText.text.clear()
                binding.lakeEditText.text.clear()
                binding.lureEditText.text.clear()
                binding.lengthEditText.text.clear()
                binding.weightEditText.text.clear()
                binding.countyEditText.text.clear()
                binding.timeEditText.text.clear()
                binding.locationEditText.text.clear()
            }
            .addOnFailureListener {
                Log.e(ContentValues.TAG, "Error adding document")
            }
    }
}
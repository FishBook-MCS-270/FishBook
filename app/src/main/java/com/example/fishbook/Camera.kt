package com.example.fishbook

import android.app.ProgressDialog
import android.content.ContentValues.TAG
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.fishbook.databinding.FragmentCameraBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

/**
 * A simple [Fragment] subclass.
 * Use the [camera.newInstance] factory method to
 * create an instance of this fragment.
 */
//testing

data class CatchDetails(
    var species: String = "",
    var lake: String = "",
    var remoteUri: String = "",
    var localUri: String = ""
)

class Camera : Fragment() {

    private lateinit var binding: FragmentCameraBinding

    private var photoName: String? = null
    private lateinit var photoFile: File
    lateinit var ImageUri: Uri

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
        binding = FragmentCameraBinding.inflate(layoutInflater, container, false)

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

        /*binding.speciesButton.setOnClickListener{
            // Popup of name of species
            val popup = PopupMenu(requireContext(), binding.speciesButton)

            // Example names, use species from data
            val speciesNames = arrayOf("Salmon", "Trout", "Bass")

            for (i in speciesNames.indices) {
                popup.menu.add(speciesNames[i]).setOnMenuItemClickListener { menuItem ->
                    // Set the text of the speciesEditText to the selected species
                    binding.speciesEditText.setText(menuItem.title)
                    true
                }
            }
            popup.show()
        }*/

        /*binding.lakeButton.setOnClickListener{
            // Popup of Lakes
            val popup = PopupMenu(requireContext(), binding.lakeButton)

            // Example names, use lakes from data
            val lakeNames = arrayOf("Lake Nokomis", "Lake Vermillion", "Lake Minnetonka")

            for (i in lakeNames.indices) {
                popup.menu.add(lakeNames[i]).setOnMenuItemClickListener { menuItem ->
                    // Set the text of the speciesEditText to the selected species
                    binding.lakeEditText.setText(menuItem.title)
                    true
                }
            }
            popup.show()
        }*/

        binding.uploadButton.setOnClickListener{
            uploadImage()
        }
        return binding.root
    }

    private var takePhotoLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { didTakePhoto: Boolean ->
        if (didTakePhoto && photoName != null) {
            binding.fishImage.setImageURI(FileProvider.getUriForFile(
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


    private fun uploadCatchDetails(catchDetails: CatchDetails) {
        val db = Firebase.firestore
        db.collection("catchDetails")
            .add(catchDetails)
            .addOnSuccessListener {
                Log.i(TAG, "Successfully added catch details")
                binding.speciesEditText.text.clear()
                binding.lakeEditText.text.clear()
            }
            .addOnFailureListener {
                Log.e(TAG, "Error adding document")
            }
    }
}
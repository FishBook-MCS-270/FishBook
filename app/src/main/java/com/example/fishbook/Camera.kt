package com.example.fishbook

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.fishbook.databinding.FragmentCameraBinding
import java.io.File
import java.util.*

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER


/**
 * A simple [Fragment] subclass.
 * Use the [camera.newInstance] factory method to
 * create an instance of this fragment.
 */
//testing
class Camera : Fragment() {

    private lateinit var binding: FragmentCameraBinding

    private var photoName: String? = null
    private lateinit var photoFile: File
    private var cameraLaunched = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        takePhotoLauncher = registerForActivityResult(
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
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentCameraBinding.inflate(layoutInflater, container, false)

        if (!cameraLaunched) {
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
            cameraLaunched = true
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

        binding.speciesButton.setOnClickListener{
            // Popup of name of species
            val popup = PopupMenu(requireContext(), binding.speciesButton)

            // Example names, use species from data
            val speciesNames = arrayOf("Salmon", "Trout", "Bass", "Tuna")

            for (i in speciesNames.indices) {
                popup.menu.add(speciesNames[i]).setOnMenuItemClickListener { menuItem ->
                    // Set the text of the speciesEditText to the selected species
                    binding.speciesEditText.setText(menuItem.title)
                    true
                }
            }
            popup.show()
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
}

package com.example.fishbook

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
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

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        cameraLaunched = true
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




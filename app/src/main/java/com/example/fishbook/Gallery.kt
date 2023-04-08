package com.example.fishbook

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.fishbook.databinding.ActivityStorageBinding
import com.example.fishbook.databinding.FragmentGalleryBinding
import com.google.android.material.bottomnavigation.BottomNavigationView


/**
 * A simple [Fragment] subclass.
 * Use the [Gallery.newInstance] factory method to
 * create an instance of this fragment.
 */
class Gallery : Fragment() {


    private var _binding: FragmentGalleryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?




    ): View? {



        _binding = FragmentGalleryBinding.inflate(inflater, container, false)

        binding.uploadbutton.setOnClickListener{
            val intent = Intent(activity, storageActivity::class.java)
            startActivity(intent)
        }

        return binding.root



    }






}
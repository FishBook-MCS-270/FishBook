package com.example.fishbook.gallery

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.example.fishbook.databinding.FragmentViewRecordBinding
import com.squareup.picasso.Picasso
import androidx.navigation.fragment.navArgs
import com.example.fishbook.gallery.ViewRecordFragmentArgs


class ViewRecordFragment : Fragment() {
    private var _binding: FragmentViewRecordBinding? = null
    private val binding get() = _binding!!
    private val args: ViewRecordFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentViewRecordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val gridImage = args.gridImage
        updateUi(gridImage)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun updateUi(gridImage: GridImage) {
        binding.apply {
            Picasso.get().load(gridImage.url).into(largeImage)
        }
    }
}

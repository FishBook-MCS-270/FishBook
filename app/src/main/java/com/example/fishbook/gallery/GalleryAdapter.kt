package com.example.fishbook.gallery

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.navigation.findNavController
import com.example.fishbook.databinding.ItemImageBinding
import com.squareup.picasso.Picasso

class GalleryAdapter(
    private val context: Context,
    private val gridImages: List<GridImage>,
    private val onClick: (GridImage) -> Unit
) : BaseAdapter() {
    override fun getCount(): Int {
        return gridImages.size
    }

    override fun getItem(position: Int): Any {
        return gridImages[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding: ItemImageBinding

        if (convertView == null) {
            val inflater = LayoutInflater.from(context)
            binding = ItemImageBinding.inflate(inflater, parent, false)
            binding.root.tag = binding
        } else {
            binding = convertView.tag as ItemImageBinding
        }

        val gridImage = getItem(position) as GridImage
        Picasso.get().load(gridImage.url).into(binding.gridImage)

        // Handle click event for the grid item
        binding.root.setOnClickListener {
            val action = GalleryDirections.showRecord(gridImage)
            it.findNavController().navigate(action)
        }

        return binding.root
    }
}
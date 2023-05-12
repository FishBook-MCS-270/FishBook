package com.example.fishbook.gallery

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.navigation.findNavController
import com.example.fishbook.databinding.ItemImageBinding
import com.example.fishbook.record.CatchDetails
import com.squareup.picasso.Picasso

class GalleryAdapter(
    private val context: Context,
    private var gridImages: List<CatchDetails>,
    private val onClick: (CatchDetails) -> Unit
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

        val catchDetail = getItem(position) as CatchDetails
        //trying to lower quality for faster load times
        val targetWidth = 175
        val targetHeight = 175
        //fixes nullexception
        if (catchDetail.remoteUri.isNotEmpty()) {
            Picasso.get()
                .load(catchDetail.remoteUri)
                .resize(targetWidth, targetHeight)
                .centerCrop()
                .into(binding.gridImage)        }
        else if (catchDetail.localUri.isNotEmpty()) {
            Picasso.get()
                .load(catchDetail.remoteUri)
                .resize(targetWidth, targetHeight)
                .centerCrop()
                .into(binding.gridImage)        }

        // Handles click event for the grid item
        binding.root.setOnClickListener {
            val action = GalleryDirections.showRecord(catchDetail)
            it.findNavController().navigate(action)
        }

        return binding.root
    }
    fun updateData(newData: List<CatchDetails>) {
        gridImages = newData.filter { it.localUri.isNotEmpty() }
        notifyDataSetChanged()
    }
}





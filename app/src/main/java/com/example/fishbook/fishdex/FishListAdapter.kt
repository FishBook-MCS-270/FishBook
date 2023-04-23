package com.example.fishbook.fishdex

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

import android.graphics.Color
import android.graphics.PorterDuff
import android.widget.Toast

import com.example.fishbook.databinding.ItemFishBinding
import java.util.*

class FishHolder(
    private val binding: ItemFishBinding
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(fish: Species, onFishClicked: (fishId: UUID) -> Unit) {
        binding.fishName.text = fish.species_name
        binding.fishImage.setImageResource(fish.image)

        if (fish.caught_flag) {
            binding.fishName.text = fish.species_name
            binding.fishImage.clearColorFilter()
        } else {
            binding.fishName.text = "???"
            binding.fishImage.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP)
        }

        binding.root.setOnClickListener {
            if (fish.caught_flag) {
                onFishClicked(fish.id)
            } else {
                Toast.makeText(
                    it.context,
                    "Fish not caught yet!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
    class FishListAdapter(
        private val fishSpecies: List<Species>,
        private val onFishClicked: (fishId: UUID) -> Unit
    ) : RecyclerView.Adapter<FishHolder>() {
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): FishHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ItemFishBinding.inflate(inflater, parent, false)
            return FishHolder(binding)
        }

        override fun onBindViewHolder(holder: FishHolder, position: Int) {
            val fish = fishSpecies[position]
            holder.bind(fish, onFishClicked)
        }

        override fun getItemCount() = fishSpecies.size
    }

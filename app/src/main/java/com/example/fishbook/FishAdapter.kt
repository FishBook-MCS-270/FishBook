package com.example.fishbook

import android.graphics.Color
import android.graphics.PorterDuff
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fishbook.FishSpecies
import com.example.fishbook.R

class FishAdapter(
    var fishList: List<FishSpecies>, // Changed from List to MutableList
    private val onItemClickListener: (Int) -> Unit
) : RecyclerView.Adapter<FishAdapter.FishViewHolder>() {


    inner class FishViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val fishImage: ImageView = itemView.findViewById(R.id.fish_image)
        val fishName: TextView = itemView.findViewById(R.id.fish_name)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FishViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_fish, parent, false)
        return FishViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: FishViewHolder, position: Int) {
        val currentItem = fishList[position]
        Log.d("FishAdapter", "Binding position: $position, species_name: ${currentItem.species_name}")

        holder.fishImage.setImageResource(currentItem.image)
        holder.fishName.text = currentItem.species_name

        if (currentItem.caught_flag) {
            holder.fishName.text = currentItem.species_name
            holder.fishImage.clearColorFilter()

        } else {
            holder.fishName.text = "???"
            holder.fishImage.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP)
        }
        //add on-clicklistener functionality
        holder.itemView.setOnClickListener {
            onItemClickListener(currentItem.id)
        }
    }
    //for recylcer view to know how many to display
    override fun getItemCount() = fishList.size


}

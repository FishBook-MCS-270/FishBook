import android.graphics.Color
import android.graphics.PorterDuff
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
    private val fishList: List<FishSpecies>,
    //pass-in functionality, specify in fishdex fragment
    private val onItemClickListener: (FishSpecies) -> Unit
) : RecyclerView.Adapter<FishAdapter.FishViewHolder>() {

    // ViewHolder for each fish in the RecyclerView

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

        holder.fishImage.setImageResource(currentItem.image)

        if (currentItem.caught_flag) {
            holder.fishName.text = currentItem.species_name
            holder.fishImage.clearColorFilter()

        } else {
            holder.fishName.text = "???"
            holder.fishImage.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP)
        }
        //add on-clicklistener functionality
        holder.itemView.setOnClickListener {
            onItemClickListener(currentItem)
        }
    }
    //for recylcer view to know how many to display
    override fun getItemCount() = fishList.size


}

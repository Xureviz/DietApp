import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.trab3.R
import com.example.trab3.entities.EntityDiet


class DietListAdapter(
    private val context: Activity,
    var diets: List<EntityDiet>,
    private val onDeleteClickListener: OnDeleteClickListener
) : RecyclerView.Adapter<DietListAdapter.DietViewHolder>() {

    inner class DietViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleText: TextView = itemView.findViewById(R.id.title)
        val deleteButton: ImageButton = itemView.findViewById(R.id.delete_button)

        init {
            deleteButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onDeleteClickListener.onDeleteClick(position)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DietViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(R.layout.diet_list_item, parent, false)
        return DietViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: DietViewHolder, position: Int) {
        val diet = diets[position]
        holder.titleText.text = diet.name
    }

    override fun getItemCount(): Int {
        return diets.size
    }

    fun updateDiets(newDiets: List<EntityDiet>) {
        diets = newDiets
        notifyDataSetChanged()
    }

    interface OnDeleteClickListener {
        fun onDeleteClick(position: Int)
    }
}

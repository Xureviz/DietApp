import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.trab3.R
import com.example.trab3.entities.EntityDiet

class DietListAdapter(private val context: Activity, private var diets: List<EntityDiet>)
    : ArrayAdapter<EntityDiet>(context, R.layout.diet_list_item, diets) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = context.layoutInflater
        val rowView = inflater.inflate(R.layout.diet_list_item, null, true)

        val titleText = rowView.findViewById(R.id.title) as TextView

        titleText.text = diets[position].name

        return rowView
    }

    fun setDiets(newDiets: List<EntityDiet>) {
        this.diets = newDiets
        notifyDataSetChanged()
    }
}

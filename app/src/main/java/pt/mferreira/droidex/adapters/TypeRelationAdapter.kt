package pt.mferreira.droidex.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.move_item.view.*
import kotlinx.android.synthetic.main.type_item.view.*
import pt.mferreira.droidex.R

class TypeRelationAdapter (private val context: Context, private val types: List<String>) : RecyclerView.Adapter<TypeRelationAdapter.MyViewHolder>() {
    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.setOnClickListener {}
        }

        private var currentType: String? = null
        private var currentPosition: Int = 0

        fun setData (type: String?, position: Int) {
            itemView.tvTypeName.text = type?.let { formatName(it) }
            colorize(itemView, itemView.tvTypeName.text.toString().toLowerCase().trim())

            currentType = type
            currentPosition = position
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TypeRelationAdapter.MyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.type_item, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: TypeRelationAdapter.MyViewHolder, position: Int) {
        val type = types[position]
        holder.setData(type, position)
    }

    override fun getItemCount(): Int {
        return types.size
    }

    /**
     * @param name String to format.
     */
    private fun formatName(name: String): String {
        var formatted = ""

        val split = name.split("-").toMutableList()
        for (i in split.indices) {
            split[i] = "${split[i].substring(0, 1).toUpperCase()}${split[i].substring(1)}"
            formatted = "$formatted ${split[i]}"
        }

        return formatted
    }

    private fun colorize(itemView: View, type: String) {
        when (type) {
            "bug" -> {
                itemView.tlType.setBackgroundColor(Color.parseColor("#A8B820"))
            }

            "dark" -> {
                itemView.tlType.setBackgroundColor(Color.parseColor("#705848"))
            }

            "dragon" -> {
                itemView.tlType.setBackgroundColor(Color.parseColor("#7038F8"))
            }

            "electric" -> {
                itemView.tlType.setBackgroundColor(Color.parseColor("#F8D030"))
            }

            "fairy" -> {
                itemView.tlType.setBackgroundColor(Color.parseColor("#EE99AC"))
            }

            "fire" -> {
                itemView.tlType.setBackgroundColor(Color.parseColor("#F08030"))
            }

            "fighting" -> {
                itemView.tlType.setBackgroundColor(Color.parseColor("#C03028"))
            }

            "flying" -> {
                itemView.tlType.setBackgroundColor(Color.parseColor("#A890F0"))
            }

            "grass" -> {
                itemView.tlType.setBackgroundColor(Color.parseColor("#78C850"))
            }

            "ghost" -> {
                itemView.tlType.setBackgroundColor(Color.parseColor("#705898"))
            }

            "ground" -> {
                itemView.tlType.setBackgroundColor(Color.parseColor("#E0C068"))
            }

            "ice" -> {
                itemView.tlType.setBackgroundColor(Color.parseColor("#98D8D8"))
            }

            "normal" -> {
                itemView.tlType.setBackgroundColor(Color.parseColor("#A8A878"))
            }

            "poison" -> {
                itemView.tlType.setBackgroundColor(Color.parseColor("#A040A0"))
            }

            "water" -> {
                itemView.tlType.setBackgroundColor(Color.parseColor("#6890F0"))
            }

            "psychic" -> {
                itemView.tlType.setBackgroundColor(Color.parseColor("#F85888"))
            }

            "rock" -> {
                itemView.tlType.setBackgroundColor(Color.parseColor("#B8A038"))
            }

            "steel" -> {
                itemView.tlType.setBackgroundColor(Color.parseColor("#B8B8D0"))
            }
        }
    }
}
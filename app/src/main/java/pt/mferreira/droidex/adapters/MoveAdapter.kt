package pt.mferreira.droidex.adapters

import pt.mferreira.droidex.models.move.Move

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.move_item.view.*
import pt.mferreira.droidex.R

class MoveAdapter (private val context: Context, private val move: List<Move>) : RecyclerView.Adapter<MoveAdapter.MyViewHolder>() {
    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.setOnClickListener {

            }
        }

        private var currentMove: Move? = null
        private var currentPosition: Int = 0

        fun setData (move: Move?, position: Int) {
            move?.let {
                itemView.tvMoveName.text = formatName(move.name)
                itemView.tvMovePower.text = "${move.power}"
            }

            currentMove = move
            currentPosition = position
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoveAdapter.MyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.move_item, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MoveAdapter.MyViewHolder, position: Int) {
        val move = move[position]
        holder.setData(move, position)
    }

    override fun getItemCount(): Int {
        return move.size
    }

    /**
     * Format a game or ability name that contains a '-'.
     *
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
}
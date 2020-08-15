package pt.mferreira.droidex.adapters

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.move_item.view.*
import pt.mferreira.droidex.AbilitySheetFragment
import pt.mferreira.droidex.MoveSheetFragment
import pt.mferreira.droidex.R
import pt.mferreira.droidex.models.move.Move

class MoveAdapter (private val context: Context, private val move: List<Move>) : RecyclerView.Adapter<MoveAdapter.MyViewHolder>() {
    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.setOnClickListener {
                val bundle = Bundle()
                bundle.putSerializable("move", currentMove)

                val moveSheetFragment = MoveSheetFragment()
                moveSheetFragment.arguments = bundle

                moveSheetFragment.show((context as AppCompatActivity).supportFragmentManager, moveSheetFragment.tag)
            }
        }

        private var currentMove: Move? = null
        private var currentPosition: Int = 0

        fun setData (move: Move?, position: Int) {
            move?.let {
                itemView.tvMoveName.text = formatName(move.name)
                if (move.power > 0)
                    itemView.tvMovePower.text = "${move.power}"
                else
                    itemView.tvMovePower.text = "-"

                if (move.accuracy > 0)
                    itemView.tvMoveAccuracy.text = "${move.accuracy}"
                else
                    itemView.tvMoveAccuracy.text = "-"

                itemView.tvMoveType.text = formatName(move.type.name)
                colorizeType(itemView, move.type.name)

                itemView.tvMoveClass.text = formatName(move.damageClass.name).toUpperCase()
                when (move.damageClass.name) {
                    "physical" -> {
                        itemView.tvMoveClass.setBackgroundColor(Color.parseColor("#C92112"))
                    }

                    "special" -> {
                        itemView.tvMoveClass.setBackgroundColor(Color.parseColor("#4F5870"))
                    }

                    "status" -> {
                        itemView.tvMoveClass.setBackgroundColor(Color.parseColor("#DCDCDA"))
                    }
                }

                itemView.tvMovePP.text = "${move.pp}"
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

    private fun colorizeType(itemView: View, type: String) {
        when (type) {
            "bug" -> {
                itemView.tvMoveType.setBackgroundColor(Color.parseColor("#A8B820"))
            }

            "dark" -> {
                itemView.tvMoveType.setBackgroundColor(Color.parseColor("#705848"))
            }

            "dragon" -> {
                itemView.tvMoveType.setBackgroundColor(Color.parseColor("#7038F8"))
            }

            "electric" -> {
                itemView.tvMoveType.setBackgroundColor(Color.parseColor("#F8D030"))
            }

            "fairy" -> {
                itemView.tvMoveType.setBackgroundColor(Color.parseColor("#EE99AC"))
            }

            "fire" -> {
                itemView.tvMoveType.setBackgroundColor(Color.parseColor("#F08030"))
            }

            "fighting" -> {
                itemView.tvMoveType.setBackgroundColor(Color.parseColor("#C03028"))
            }

            "flying" -> {
                itemView.tvMoveType.setBackgroundColor(Color.parseColor("#A890F0"))
            }

            "grass" -> {
                itemView.tvMoveType.setBackgroundColor(Color.parseColor("#78C850"))
            }

            "ghost" -> {
                itemView.tvMoveType.setBackgroundColor(Color.parseColor("#705898"))
            }

            "ground" -> {
                itemView.tvMoveType.setBackgroundColor(Color.parseColor("#E0C068"))
            }

            "ice" -> {
                itemView.tvMoveType.setBackgroundColor(Color.parseColor("#98D8D8"))
            }

            "normal" -> {
                itemView.tvMoveType.setBackgroundColor(Color.parseColor("#A8A878"))
            }

            "poison" -> {
                itemView.tvMoveType.setBackgroundColor(Color.parseColor("#A040A0"))
            }

            "water" -> {
                itemView.tvMoveType.setBackgroundColor(Color.parseColor("#6890F0"))
            }

            "psychic" -> {
                itemView.tvMoveType.setBackgroundColor(Color.parseColor("#F85888"))
            }

            "rock" -> {
                itemView.tvMoveType.setBackgroundColor(Color.parseColor("#B8A038"))
            }

            "steel" -> {
                itemView.tvMoveType.setBackgroundColor(Color.parseColor("#B8B8D0"))
            }
        }
    }
}
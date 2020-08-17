package pt.mferreira.droidex.adapters

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.opengl.Visibility
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.dex_item.view.*
import pt.mferreira.droidex.DetailsActivity
import pt.mferreira.droidex.R
import pt.mferreira.droidex.models.pokemon.Pokemon

class DexAdapter (private val context: Context? = null, private val pokemon: List<Pokemon>? = null) : RecyclerView.Adapter<DexAdapter.MyViewHolder>() {
    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.setOnClickListener {
                val intent = Intent(context, DetailsActivity::class.java)
                intent.putExtra("details", currentPokemon)
                if (context != null) context.startActivity(intent)
            }
        }

        private var currentPokemon: Pokemon? = null
        private var currentPosition: Int = 0

        fun setData (pokemon: Pokemon?, position: Int) {
            pokemon?.let {
                // Name and number.
                if (pokemon.id < 10)
                    itemView.tvDexNumber.text = "#00${pokemon.id}"
                else if (pokemon.id in 10..99)
                    itemView.tvDexNumber.text = "#0${pokemon.id}"
                else
                    itemView.tvDexNumber.text = "#${pokemon.id}"

                itemView.tvDexName.text = "${pokemon.name.substring(0, 1).toUpperCase()}${pokemon.name.substring(1)}"

                // Load type 1.
                itemView.tvDexType1.text = pokemon.types[0].type.name.toUpperCase()

                // Load type 2.
                if (pokemon.types.size == 2) {
                    itemView.tvDexType2.text = pokemon.types[1].type.name.toUpperCase()
                    itemView.tvDexType2.visibility = View.VISIBLE
                } else
                    itemView.tvDexType2.visibility = View.GONE

                // Load image.
                Picasso.get().load(pokemon.sprites.other.officialArtwork.frontDefault).into(itemView.ivDexSprite, object : Callback {
                    override fun onSuccess() {
                        itemView.dexSpriteProgress.visibility = View.GONE
                    }

                    override fun onError(e: Exception?) {

                    }
                })

                when (pokemon.types[0].type.name) {
                    "bug" -> {
                        itemView.llDexEntry.setBackgroundColor(Color.parseColor("#A8B820"))
                    }

                    "dark" -> {
                        itemView.llDexEntry.setBackgroundColor(Color.parseColor("#705848"))
                    }

                    "dragon" -> {
                        itemView.llDexEntry.setBackgroundColor(Color.parseColor("#7038F8"))
                    }

                    "electric" -> {
                        itemView.llDexEntry.setBackgroundColor(Color.parseColor("#F8D030"))
                    }

                    "fairy" -> {
                        itemView.llDexEntry.setBackgroundColor(Color.parseColor("#EE99AC"))
                    }

                    "fire" -> {
                        itemView.llDexEntry.setBackgroundColor(Color.parseColor("#F08030"))
                    }

                    "fighting" -> {
                        itemView.llDexEntry.setBackgroundColor(Color.parseColor("#C03028"))
                    }

                    "flying" -> {
                        itemView.llDexEntry.setBackgroundColor(Color.parseColor("#A890F0"))
                    }

                    "grass" -> {
                        itemView.llDexEntry.setBackgroundColor(Color.parseColor("#78C850"))
                    }

                    "ghost" -> {
                        itemView.llDexEntry.setBackgroundColor(Color.parseColor("#705898"))
                    }

                    "ground" -> {
                        itemView.llDexEntry.setBackgroundColor(Color.parseColor("#E0C068"))
                    }

                    "ice" -> {
                        itemView.llDexEntry.setBackgroundColor(Color.parseColor("#98D8D8"))
                    }

                    "normal" -> {
                        itemView.llDexEntry.setBackgroundColor(Color.parseColor("#A8A878"))
                    }

                    "poison" -> {
                        itemView.llDexEntry.setBackgroundColor(Color.parseColor("#A040A0"))
                    }

                    "water" -> {
                        itemView.llDexEntry.setBackgroundColor(Color.parseColor("#6890F0"))
                    }

                    "psychic" -> {
                        itemView.llDexEntry.setBackgroundColor(Color.parseColor("#F85888"))
                    }

                    "rock" -> {
                        itemView.llDexEntry.setBackgroundColor(Color.parseColor("#B8A038"))
                    }

                    "steel" -> {
                        itemView.llDexEntry.setBackgroundColor(Color.parseColor("#B8B8D0"))
                    }
                }
            }

            currentPokemon = pokemon
            currentPosition = position
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DexAdapter.MyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.dex_item, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: DexAdapter.MyViewHolder, position: Int) {
        val pokemon = pokemon?.get(position)
        holder.setData(pokemon, position)
    }

    override fun getItemCount(): Int {
        if (pokemon != null) return pokemon.size
        return 0
    }
}
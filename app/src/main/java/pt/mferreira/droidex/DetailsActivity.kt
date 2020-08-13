package pt.mferreira.droidex

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.google.gson.Gson
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_details.*
import pt.mferreira.droidex.models.global.PokemonPage
import pt.mferreira.droidex.models.pokemon.Pokemon
import pt.mferreira.droidex.models.pokemon.PokemonSpecies
import pt.mferreira.droidex.singletons.VolleySingleton

class DetailsActivity : AppCompatActivity() {
    private lateinit var context: Context
    lateinit var pokemon: Pokemon

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
        context = this

        pokemon = intent.getSerializableExtra("details") as Pokemon

        val window: Window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

        when (pokemon.types[0].type.name) {
            "bug" -> {
                rlDetails.setBackgroundColor(Color.parseColor("#A8B820"))
                window.statusBarColor = Color.parseColor("#A8B820")
            }

            "dark" -> {
                rlDetails.setBackgroundColor(Color.parseColor("#705848"))
                window.statusBarColor = Color.parseColor("#705848")
            }

            "dragon" -> {
                rlDetails.setBackgroundColor(Color.parseColor("#7038F8"))
                window.statusBarColor = Color.parseColor("#7038F8")
            }

            "electric" -> {
                rlDetails.setBackgroundColor(Color.parseColor("#F8D030"))
                window.statusBarColor = Color.parseColor("#F8D030")
            }

            "fairy" -> {
                rlDetails.setBackgroundColor(Color.parseColor("#EE99AC"))
                window.statusBarColor = Color.parseColor("#EE99AC")
            }

            "fire" -> {
                rlDetails.setBackgroundColor(Color.parseColor("#F08030"))
                window.statusBarColor = Color.parseColor("#F08030")
            }

            "fighting" -> {
                rlDetails.setBackgroundColor(Color.parseColor("#C03028"))
                window.statusBarColor = Color.parseColor("#C03028")
            }

            "flying" -> {
                rlDetails.setBackgroundColor(Color.parseColor("#A890F0"))
                window.statusBarColor = Color.parseColor("#A890F0")
            }

            "grass" -> {
                rlDetails.setBackgroundColor(Color.parseColor("#78C850"))
                window.statusBarColor = Color.parseColor("#78C850")
            }

            "ghost" -> {
                rlDetails.setBackgroundColor(Color.parseColor("#705898"))
                window.statusBarColor = Color.parseColor("#705898")
            }

            "ground" -> {
                rlDetails.setBackgroundColor(Color.parseColor("#E0C068"))
                window.statusBarColor = Color.parseColor("#E0C068")
            }

            "ice" -> {
                rlDetails.setBackgroundColor(Color.parseColor("#98D8D8"))
                window.statusBarColor = Color.parseColor("#98D8D8")
            }

            "normal" -> {
                rlDetails.setBackgroundColor(Color.parseColor("#A8A878"))
                window.statusBarColor = Color.parseColor("#A8A878")
            }

            "poison" -> {
                rlDetails.setBackgroundColor(Color.parseColor("#A040A0"))
                window.statusBarColor = Color.parseColor("#A040A0")
            }

            "water" -> {
                rlDetails.setBackgroundColor(Color.parseColor("#6890F0"))
                window.statusBarColor = Color.parseColor("#6890F0")
            }

            "psychic" -> {
                rlDetails.setBackgroundColor(Color.parseColor("#F85888"))
                window.statusBarColor = Color.parseColor("#F85888")
            }

            "rock" -> {
                rlDetails.setBackgroundColor(Color.parseColor("#B8A038"))
                window.statusBarColor = Color.parseColor("#B8A038")
            }

            "steel" -> {
                rlDetails.setBackgroundColor(Color.parseColor("#B8B8D0"))
                window.statusBarColor = Color.parseColor("#B8B8D0")
            }
        }

        if (pokemon.id < 10)
            tvDetailsNumber.text = "#00${pokemon.id}"
        else if (pokemon.id in 10..99)
            tvDetailsNumber.text = "#0${pokemon.id}"
        else
            tvDetailsNumber.text = "#${pokemon.id}"

        tvDetailsName.text = "${pokemon.name.substring(0, 1).toUpperCase()}${pokemon.name.substring(1)}"

        // Load type 1.
        tvDetailsType1.text = pokemon.types[0].type.name.toUpperCase()
        colorizeType(cvDetailsType1, pokemon.types[0].type.name)

        // Load type 2.
        if (pokemon.types.size == 2) {
            tvDetailsType2.text = pokemon.types[1].type.name.toUpperCase()
            colorizeType(cvDetailsType2, pokemon.types[1].type.name)
            tvDetailsType2.visibility = View.VISIBLE
        } else
            tvDetailsType2.visibility = View.GONE

        // Load image.
        Picasso.get().load(pokemon.sprites.other.officialArtwork.frontDefault).into(ivDetailsSprite, object :
            Callback {
            override fun onSuccess() {
                detailsSpriteProgress.visibility = View.GONE
            }

            override fun onError(e: Exception?) {

            }
        })

        downloadSpecies(pokemon.species.url)

        // Format ability strings.
        tvDetailsAbilitiesSlotHidden.text = "No hidden ability."
        val abilities = mutableListOf<String>()
        for (i in pokemon.abilities.indices) {
            if (pokemon.abilities[i].ability.name.contains("-")) {
                val split = pokemon.abilities[i].ability.name.split("-").toMutableList()
                var new = ""

                for (j in split.indices) {
                    split[j] = "${split[j].substring(0, 1).toUpperCase()}${split[j].substring(1)}"
                    new = "$new ${split[j]}"
                }

                if (pokemon.abilities[i].isHidden)
                    tvDetailsAbilitiesSlotHidden.text = new
                else
                    abilities.add(new)
            } else {
                if (pokemon.abilities[i].isHidden)
                    tvDetailsAbilitiesSlotHidden.text = "${pokemon.abilities[i].ability.name.substring(0, 1).toUpperCase()}${pokemon.abilities[i].ability.name.substring(1)}"
                else
                    abilities.add("${pokemon.abilities[i].ability.name.substring(0, 1).toUpperCase()}${pokemon.abilities[i].ability.name.substring(1)}")
            }
        }

        println("lmao report: ${abilities}")

        tvDetailsAbilitiesSlot1.text = abilities[0]
        if (abilities.size == 2) {
            tvDetailsAbilitiesSlot2.text = abilities[1]
        } else
            tvDetailsAbilitiesSlot2.text = "No slot 2 ability."
    }

    /**
     * Download a specific Pokémon's species' details.
     *
     * @param url URL to download from.
     */
    private fun downloadSpecies(url: String) {
        val jor = JsonObjectRequest(
            Request.Method.GET, url, null,
            Response.Listener {
                val resp = Gson().fromJson(it.toString(), PokemonSpecies::class.java)

                // Fill species.
                tvDetailsSpecies.text = resp.genera[7].genus

                // Fill species panel.
                var flavorText = ""
                var version = ""
                for (element in resp.flavorText) {
                    if (element.language.name == "en") {
                        flavorText = element.flavorText
                        version = element.version.name
                    }
                }

                // Format version string.
                flavorText = flavorText.replace("\n", " ")
                if (version.contains("-")) {
                    val split = version.split("-").toMutableList()
                    version = ""

                    for (i in split.indices) {
                        split[i] = "${split[i].substring(0, 1).toUpperCase()}${split[i].substring(1)}"
                        version = "${version} ${split[i]}"
                    }
                } else {
                    version = "${version.substring(0, 1).toUpperCase()}${version.substring(1)}"
                }

                tvDetailsDexEntryGame.text = "Pokédex entry from${version}."
                tvDetailsSpeciesFlavorText.text = flavorText

                tvDetailsHeight.text = "${((pokemon.height).toFloat() / 10)}m"
                tvDetailsWeight.text = "${((pokemon.weight).toFloat() / 10)}kg"
            },
            Response.ErrorListener {
                Toast.makeText(context,
                    "Error while performing request. Please check your connection or try again later.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )

        VolleySingleton.getInstance(context).addToRequestQueue(jor)
    }

    private fun colorizeType(cv: CardView, type: String) {
        when (type) {
            "bug" -> {
                cv.setCardBackgroundColor(Color.parseColor("#A8B820"))
            }

            "dark" -> {
                cv.setCardBackgroundColor(Color.parseColor("#705848"))
            }

            "dragon" -> {
                cv.setCardBackgroundColor(Color.parseColor("#7038F8"))
            }

            "electric" -> {
                cv.setCardBackgroundColor(Color.parseColor("#F8D030"))
            }

            "fairy" -> {
                cv.setCardBackgroundColor(Color.parseColor("#EE99AC"))
            }

            "fire" -> {
                cv.setCardBackgroundColor(Color.parseColor("#F08030"))
            }

            "fighting" -> {
                cv.setCardBackgroundColor(Color.parseColor("#C03028"))
            }

            "flying" -> {
                cv.setCardBackgroundColor(Color.parseColor("#A890F0"))
            }

            "grass" -> {
                cv.setCardBackgroundColor(Color.parseColor("#78C850"))
            }

            "ghost" -> {
                cv.setCardBackgroundColor(Color.parseColor("#705898"))
            }

            "ground" -> {
                cv.setCardBackgroundColor(Color.parseColor("#E0C068"))
            }

            "ice" -> {
                cv.setCardBackgroundColor(Color.parseColor("#98D8D8"))
            }

            "normal" -> {
                cv.setCardBackgroundColor(Color.parseColor("#A8A878"))
            }

            "poison" -> {
                cv.setCardBackgroundColor(Color.parseColor("#A040A0"))
            }

            "water" -> {
                cv.setCardBackgroundColor(Color.parseColor("#6890F0"))
            }

            "psychic" -> {
                cv.setCardBackgroundColor(Color.parseColor("#F85888"))
            }

            "rock" -> {
                cv.setCardBackgroundColor(Color.parseColor("#B8A038"))
            }

            "steel" -> {
                cv.setCardBackgroundColor(Color.parseColor("#B8B8D0"))
            }
        }
    }
}
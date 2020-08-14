package pt.mferreira.droidex

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
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
import kotlinx.android.synthetic.main.ability_sheet_fragment.*
import kotlinx.android.synthetic.main.activity_details.*
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

        // Get selected Pokémon.
        pokemon = intent.getSerializableExtra("details") as Pokemon

        // Hide status bar.
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

        // Format Pokémon's dex number (add 0s e.g. 001, 010).
        if (pokemon.id < 10) tvDetailsNumber.text = "#00${pokemon.id}"
        else if (pokemon.id in 10..99) tvDetailsNumber.text = "#0${pokemon.id}"
        else tvDetailsNumber.text = "#${pokemon.id}"

        // Format Pokémon name (first character should be upper case).
        val fc = pokemon.name.substring(0, 1).toUpperCase()
        val rest = pokemon.name.substring(1)
        tvDetailsName.text = "$fc$rest"

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

        // Load Pokémon image.
        Picasso.get().load(pokemon.sprites.other.officialArtwork.frontDefault).into(ivDetailsSprite, object : Callback {
            override fun onSuccess() { detailsSpriteProgress.visibility = View.GONE }
            override fun onError(e: Exception?) {}
        })

        downloadSpecies(pokemon.species.url)

        // Insert and format abilities and necessary.
        var hasHidden = false
        var firstSlotFilled = false
        var secondSlotFilled = false
        for (ability in pokemon.abilities) {
            var str = "${ability.ability.name.substring(0, 1).toUpperCase()}${ability.ability.name.substring(1)}"

            if (ability.isHidden) {
                hasHidden = true

                // Format ability name if necessary.
                if (ability.ability.name.contains("-"))
                    str = formatName(ability.ability.name)
                tvDetailsAbilitiesSlotHidden.text = str
            } else if (!firstSlotFilled) {
                firstSlotFilled = true

                // Format ability name if necessary.
                if (ability.ability.name.contains("-"))
                    str = formatName(ability.ability.name)
                tvDetailsAbilitiesSlot1.text = str
            } else {
                secondSlotFilled = true

                // Format ability name if necessary.
                if (ability.ability.name.contains("-"))
                    str = formatName(ability.ability.name)
                tvDetailsAbilitiesSlot2.text = str
            }
        }

        if (!secondSlotFilled)
            tvDetailsAbilitiesSlot2.text = "No slot 2 ability."

        if (!hasHidden)
            tvDetailsAbilitiesSlotHidden.text = "No hidden ability."

        tvDetailsAbilitiesSlot1.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("url", pokemon.abilities[0].ability.url)

            val abilitySheetFragment = AbilitySheetFragment()
            abilitySheetFragment.arguments = bundle

            abilitySheetFragment.show(supportFragmentManager, abilitySheetFragment.tag)
        }

        tvDetailsAbilitiesSlot2.setOnClickListener {
            if (tvDetailsAbilitiesSlot2.text != "No slot 2 ability.") {
                val bundle = Bundle()
                bundle.putString("url", pokemon.abilities[1].ability.url)

                val abilitySheetFragment = AbilitySheetFragment()
                abilitySheetFragment.arguments = bundle

                abilitySheetFragment.show(supportFragmentManager, abilitySheetFragment.tag)
            }
        }

        tvDetailsAbilitiesSlotHidden.setOnClickListener {
            if (tvDetailsAbilitiesSlotHidden.text != "No hidden ability.") {
                if (pokemon.abilities[1].isHidden) {
                    val bundle = Bundle()
                    bundle.putString("url", pokemon.abilities[1].ability.url)

                    val abilitySheetFragment = AbilitySheetFragment()
                    abilitySheetFragment.arguments = bundle

                    abilitySheetFragment.show(supportFragmentManager, abilitySheetFragment.tag)
                } else {
                    val bundle = Bundle()
                    bundle.putString("url", pokemon.abilities[2].ability.url)

                    val abilitySheetFragment = AbilitySheetFragment()
                    abilitySheetFragment.arguments = bundle

                    abilitySheetFragment.show(supportFragmentManager, abilitySheetFragment.tag)
                }
            }
        }
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

                // Fill species 7 is the english index.
                tvDetailsSpecies.text = resp.genera[7].genus

                // Get latest english description and game.
                var flavorText = ""
                var version = ""
                for (element in resp.flavorText) {
                    if (element.language.name == "en") {
                        flavorText = element.flavorText
                        version = element.version.name
                    }
                }

                flavorText = flavorText.replace("\n", " ")

                // Format version string (remove the '-' if it has one).
                version = formatName(version)

                tvDetailsDexEntryGame.text = "Pokédex entry from${version}."
                tvDetailsSpeciesFlavorText.text = flavorText

                // Convert decimetres to metres and hectograms to kilograms.
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
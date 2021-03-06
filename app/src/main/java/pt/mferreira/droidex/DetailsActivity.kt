package pt.mferreira.droidex

import android.R.attr.data
import android.content.Context
import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.google.gson.Gson
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_details.*
import org.json.JSONObject
import pt.mferreira.droidex.adapters.MoveAdapter
import pt.mferreira.droidex.adapters.TypeRelationAdapter
import pt.mferreira.droidex.models.global.PokemonPage
import pt.mferreira.droidex.models.move.Move
import pt.mferreira.droidex.models.pokemon.Pokemon
import pt.mferreira.droidex.models.pokemon.PokemonSpecies
import pt.mferreira.droidex.models.type.Type
import pt.mferreira.droidex.singletons.VolleySingleton
import java.io.BufferedOutputStream
import java.io.BufferedWriter
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class DetailsActivity : AppCompatActivity() {
    private lateinit var context: Context
    lateinit var pokemon: Pokemon
    private var moves: MutableList<Move> = ArrayList()
    private val moveAdapter = MoveAdapter(this, moves)

    private val weaknesses: MutableList<String> = ArrayList()
    private val weaknessesAdapter = TypeRelationAdapter(this, weaknesses)
    private val resistances: MutableList<String> = ArrayList()
    private val resistancesAdapter = TypeRelationAdapter(this, resistances)

    inner class AddToFavorites : AsyncTask<String, Void, Int>() {
        var success = true

        override fun doInBackground(vararg params: String): Int {
            if (params[0].isNotEmpty()) {

                val stringRequest: StringRequest = object : StringRequest(Method.POST, params[0],
                    Response.Listener { response ->

                        try {
                            val jsonObject = JSONObject(response)
                            success = true
                        } catch (ex: java.lang.Exception) {
                            success = false
                        }

                    },
                    Response.ErrorListener { error ->
                        success = false
                    }) {

                    override fun getParams(): Map<String, String> {
                        val params: MutableMap<String, String> = HashMap()
                        params["name"] = pokemon.name
                        return params
                    }
                }

                VolleySingleton.getInstance(context).addToRequestQueue(stringRequest)
            }

            return 1
        }

            override fun onPostExecute(result: Int?) {
                super.onPostExecute(result)

                if (success)
                    Toast.makeText(context, "POST request sent. Check it out at https://webhook.site/#!/1e63b79c-f943-4cd5-8472-df0e14795ded", Toast.LENGTH_LONG).show()
                else
                    Toast.makeText(context, "Could not send POST request, please try again later.", Toast.LENGTH_SHORT).show()
            }

        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
        context = this

        // Setup move RecyclerView.
        setupMoveRecyclerView()

        // Get selected Pokémon.
        pokemon = intent.getSerializableExtra("details") as Pokemon

        hideStatusBarAndColorizeBackground()
        colorizeUI()

        // Format Pokémon's dex number (add 0s e.g. 001, 010).
        if (pokemon.id < 10) tvDetailsNumber.text = "#00${pokemon.id}"
        else if (pokemon.id in 10..99) tvDetailsNumber.text = "#0${pokemon.id}"
        else tvDetailsNumber.text = "#${pokemon.id}"

        // Format Pokémon name (first character should be upper case).
        val fc = pokemon.name.substring(0, 1).toUpperCase()
        val rest = pokemon.name.substring(1)
        tvDetailsName.text = "$fc$rest"

        detailsFavorite.setOnClickListener {
            AddToFavorites().execute("https://webhook.site/1e63b79c-f943-4cd5-8472-df0e14795ded")
        }

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

        // Load base stats.
        tvDetailsHpStat.text = "${pokemon.stats[0].baseStat}"
        tvDetailsAttackStat.text = "${pokemon.stats[1].baseStat}"
        tvDetailsDefenseStat.text = "${pokemon.stats[2].baseStat}"
        tvDetailsSpecialAttackStat.text = "${pokemon.stats[3].baseStat}"
        tvDetailsSpecialDefenseStat.text = "${pokemon.stats[4].baseStat}"
        tvDetailsSpeedStat.text = "${pokemon.stats[5].baseStat}"

        var total = 0
        for (stat in pokemon.stats)
            total += stat.baseStat

        tvDetailsBaseStatsTotal.text = "$total"

        downloadByMethod("level-up")

        cvMoveMethod1.setOnClickListener {
            downloadByMethod("level-up")
        }

        cvMoveMethod2.setOnClickListener {
            downloadByMethod("machine")
        }

        cvMoveMethod3.setOnClickListener {
            downloadByMethod("egg")
        }

        cvMoveMethod4.setOnClickListener {
            downloadByMethod("tutor")
        }

        setupWeaknesses()
        setupResistances()
    }

    private fun downloadByMethod(method: String) {
        moves.clear()
        moveAdapter.notifyDataSetChanged()

        for (entry in pokemon.moves) {
            for (details in entry.details) {
                if (details.learnMethod.name == method && details.version.name == "ultra-sun-ultra-moon") {
                    downloadMoves(entry.move.url)
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

    private fun hideStatusBarAndColorizeBackground() {
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

    private fun colorizeUI() {
        when (pokemon.types[0].type.name) {
            "bug" -> {
                tvDetailsAbilitiesSlot1.setBackgroundColor(Color.parseColor("#A8B820"))
                tvDetailsAbilitiesSlot2.setBackgroundColor(Color.parseColor("#A8B820"))
                tvDetailsAbilitiesSlotHidden.setBackgroundColor(Color.parseColor("#A8B820"))

                tvDetailsHpStatLabel.setBackgroundColor(Color.parseColor("#A8B820"))
                tvDetailsAttackStatLabel.setBackgroundColor(Color.parseColor("#A8B820"))
                tvDetailsDefenseStatLabel.setBackgroundColor(Color.parseColor("#A8B820"))
                tvDetailsSpecialAttackStatLabel.setBackgroundColor(Color.parseColor("#A8B820"))
                tvDetailsSpecialDefenseStatLabel.setBackgroundColor(Color.parseColor("#A8B820"))
                tvDetailsSpeedStatLabel.setBackgroundColor(Color.parseColor("#A8B820"))
                tvDetailsBaseStatsTotal.setTextColor(Color.parseColor("#A8B820"))

                tvMoveMethod1.setBackgroundColor(Color.parseColor("#A8B820"))
                tvMoveMethod2.setBackgroundColor(Color.parseColor("#A8B820"))
                tvMoveMethod3.setBackgroundColor(Color.parseColor("#A8B820"))
                tvMoveMethod4.setBackgroundColor(Color.parseColor("#A8B820"))
            }

            "dark" -> {
                tvDetailsAbilitiesSlot1.setBackgroundColor(Color.parseColor("#705848"))
                tvDetailsAbilitiesSlot2.setBackgroundColor(Color.parseColor("#705848"))
                tvDetailsAbilitiesSlotHidden.setBackgroundColor(Color.parseColor("#705848"))

                tvDetailsHpStatLabel.setBackgroundColor(Color.parseColor("#705848"))
                tvDetailsAttackStatLabel.setBackgroundColor(Color.parseColor("#705848"))
                tvDetailsDefenseStatLabel.setBackgroundColor(Color.parseColor("#705848"))
                tvDetailsSpecialAttackStatLabel.setBackgroundColor(Color.parseColor("#705848"))
                tvDetailsSpecialDefenseStatLabel.setBackgroundColor(Color.parseColor("#705848"))
                tvDetailsSpeedStatLabel.setBackgroundColor(Color.parseColor("#705848"))
                tvDetailsBaseStatsTotal.setTextColor(Color.parseColor("#705848"))

                tvMoveMethod1.setBackgroundColor(Color.parseColor("#705848"))
                tvMoveMethod2.setBackgroundColor(Color.parseColor("#705848"))
                tvMoveMethod3.setBackgroundColor(Color.parseColor("#705848"))
                tvMoveMethod4.setBackgroundColor(Color.parseColor("#705848"))
            }

            "dragon" -> {
                tvDetailsAbilitiesSlot1.setBackgroundColor(Color.parseColor("#7038F8"))
                tvDetailsAbilitiesSlot2.setBackgroundColor(Color.parseColor("#7038F8"))
                tvDetailsAbilitiesSlotHidden.setBackgroundColor(Color.parseColor("#7038F8"))

                tvDetailsHpStatLabel.setBackgroundColor(Color.parseColor("#7038F8"))
                tvDetailsAttackStatLabel.setBackgroundColor(Color.parseColor("#7038F8"))
                tvDetailsDefenseStatLabel.setBackgroundColor(Color.parseColor("#7038F8"))
                tvDetailsSpecialAttackStatLabel.setBackgroundColor(Color.parseColor("#7038F8"))
                tvDetailsSpecialDefenseStatLabel.setBackgroundColor(Color.parseColor("#7038F8"))
                tvDetailsSpeedStatLabel.setBackgroundColor(Color.parseColor("#7038F8"))
                tvDetailsBaseStatsTotal.setTextColor(Color.parseColor("#7038F8"))

                tvMoveMethod1.setBackgroundColor(Color.parseColor("#7038F8"))
                tvMoveMethod2.setBackgroundColor(Color.parseColor("#7038F8"))
                tvMoveMethod3.setBackgroundColor(Color.parseColor("#7038F8"))
                tvMoveMethod4.setBackgroundColor(Color.parseColor("#7038F8"))
            }

            "electric" -> {
                tvDetailsAbilitiesSlot1.setBackgroundColor(Color.parseColor("#F8D030"))
                tvDetailsAbilitiesSlot2.setBackgroundColor(Color.parseColor("#F8D030"))
                tvDetailsAbilitiesSlotHidden.setBackgroundColor(Color.parseColor("#F8D030"))

                tvDetailsHpStatLabel.setBackgroundColor(Color.parseColor("#F8D030"))
                tvDetailsAttackStatLabel.setBackgroundColor(Color.parseColor("#F8D030"))
                tvDetailsDefenseStatLabel.setBackgroundColor(Color.parseColor("#F8D030"))
                tvDetailsSpecialAttackStatLabel.setBackgroundColor(Color.parseColor("#F8D030"))
                tvDetailsSpecialDefenseStatLabel.setBackgroundColor(Color.parseColor("#F8D030"))
                tvDetailsSpeedStatLabel.setBackgroundColor(Color.parseColor("#F8D030"))
                tvDetailsBaseStatsTotal.setTextColor(Color.parseColor("#F8D030"))

                tvMoveMethod1.setBackgroundColor(Color.parseColor("#F8D030"))
                tvMoveMethod2.setBackgroundColor(Color.parseColor("#F8D030"))
                tvMoveMethod3.setBackgroundColor(Color.parseColor("#F8D030"))
                tvMoveMethod4.setBackgroundColor(Color.parseColor("#F8D030"))
            }

            "fairy" -> {
                tvDetailsAbilitiesSlot1.setBackgroundColor(Color.parseColor("#EE99AC"))
                tvDetailsAbilitiesSlot2.setBackgroundColor(Color.parseColor("#EE99AC"))
                tvDetailsAbilitiesSlotHidden.setBackgroundColor(Color.parseColor("#EE99AC"))

                tvDetailsHpStatLabel.setBackgroundColor(Color.parseColor("#EE99AC"))
                tvDetailsAttackStatLabel.setBackgroundColor(Color.parseColor("#EE99AC"))
                tvDetailsDefenseStatLabel.setBackgroundColor(Color.parseColor("#EE99AC"))
                tvDetailsSpecialAttackStatLabel.setBackgroundColor(Color.parseColor("#EE99AC"))
                tvDetailsSpecialDefenseStatLabel.setBackgroundColor(Color.parseColor("#EE99AC"))
                tvDetailsSpeedStatLabel.setBackgroundColor(Color.parseColor("#EE99AC"))
                tvDetailsBaseStatsTotal.setTextColor(Color.parseColor("#EE99AC"))

                tvMoveMethod1.setBackgroundColor(Color.parseColor("#EE99AC"))
                tvMoveMethod2.setBackgroundColor(Color.parseColor("#EE99AC"))
                tvMoveMethod3.setBackgroundColor(Color.parseColor("#EE99AC"))
                tvMoveMethod4.setBackgroundColor(Color.parseColor("#EE99AC"))
            }

            "fire" -> {
                tvDetailsAbilitiesSlot1.setBackgroundColor(Color.parseColor("#F08030"))
                tvDetailsAbilitiesSlot2.setBackgroundColor(Color.parseColor("#F08030"))
                tvDetailsAbilitiesSlotHidden.setBackgroundColor(Color.parseColor("#F08030"))

                tvDetailsHpStatLabel.setBackgroundColor(Color.parseColor("#F08030"))
                tvDetailsAttackStatLabel.setBackgroundColor(Color.parseColor("#F08030"))
                tvDetailsDefenseStatLabel.setBackgroundColor(Color.parseColor("#F08030"))
                tvDetailsSpecialAttackStatLabel.setBackgroundColor(Color.parseColor("#F08030"))
                tvDetailsSpecialDefenseStatLabel.setBackgroundColor(Color.parseColor("#F08030"))
                tvDetailsSpeedStatLabel.setBackgroundColor(Color.parseColor("#F08030"))
                tvDetailsBaseStatsTotal.setTextColor(Color.parseColor("#F08030"))

                tvMoveMethod1.setBackgroundColor(Color.parseColor("#F08030"))
                tvMoveMethod2.setBackgroundColor(Color.parseColor("#F08030"))
                tvMoveMethod3.setBackgroundColor(Color.parseColor("#F08030"))
                tvMoveMethod4.setBackgroundColor(Color.parseColor("#F08030"))
            }

            "fighting" -> {
                tvDetailsAbilitiesSlot1.setBackgroundColor(Color.parseColor("#C03028"))
                tvDetailsAbilitiesSlot2.setBackgroundColor(Color.parseColor("#C03028"))
                tvDetailsAbilitiesSlotHidden.setBackgroundColor(Color.parseColor("#C03028"))

                tvDetailsHpStatLabel.setBackgroundColor(Color.parseColor("#C03028"))
                tvDetailsAttackStatLabel.setBackgroundColor(Color.parseColor("#C03028"))
                tvDetailsDefenseStatLabel.setBackgroundColor(Color.parseColor("#C03028"))
                tvDetailsSpecialAttackStatLabel.setBackgroundColor(Color.parseColor("#C03028"))
                tvDetailsSpecialDefenseStatLabel.setBackgroundColor(Color.parseColor("#C03028"))
                tvDetailsSpeedStatLabel.setBackgroundColor(Color.parseColor("#C03028"))
                tvDetailsBaseStatsTotal.setTextColor(Color.parseColor("#C03028"))

                tvMoveMethod1.setBackgroundColor(Color.parseColor("#C03028"))
                tvMoveMethod2.setBackgroundColor(Color.parseColor("#C03028"))
                tvMoveMethod3.setBackgroundColor(Color.parseColor("#C03028"))
                tvMoveMethod4.setBackgroundColor(Color.parseColor("#C03028"))
            }

            "flying" -> {
                tvDetailsAbilitiesSlot1.setBackgroundColor(Color.parseColor("#A890F0"))
                tvDetailsAbilitiesSlot2.setBackgroundColor(Color.parseColor("#A890F0"))
                tvDetailsAbilitiesSlotHidden.setBackgroundColor(Color.parseColor("#A890F0"))

                tvDetailsHpStatLabel.setBackgroundColor(Color.parseColor("#A890F0"))
                tvDetailsAttackStatLabel.setBackgroundColor(Color.parseColor("#A890F0"))
                tvDetailsDefenseStatLabel.setBackgroundColor(Color.parseColor("#A890F0"))
                tvDetailsSpecialAttackStatLabel.setBackgroundColor(Color.parseColor("#A890F0"))
                tvDetailsSpecialDefenseStatLabel.setBackgroundColor(Color.parseColor("#A890F0"))
                tvDetailsSpeedStatLabel.setBackgroundColor(Color.parseColor("#A890F0"))
                tvDetailsBaseStatsTotal.setTextColor(Color.parseColor("#A890F0"))

                tvMoveMethod1.setBackgroundColor(Color.parseColor("#A890F0"))
                tvMoveMethod2.setBackgroundColor(Color.parseColor("#A890F0"))
                tvMoveMethod3.setBackgroundColor(Color.parseColor("#A890F0"))
                tvMoveMethod4.setBackgroundColor(Color.parseColor("#A890F0"))
            }

            "grass" -> {
                tvDetailsAbilitiesSlot1.setBackgroundColor(Color.parseColor("#78C850"))
                tvDetailsAbilitiesSlot2.setBackgroundColor(Color.parseColor("#78C850"))
                tvDetailsAbilitiesSlotHidden.setBackgroundColor(Color.parseColor("#78C850"))

                tvDetailsHpStatLabel.setBackgroundColor(Color.parseColor("#78C850"))
                tvDetailsAttackStatLabel.setBackgroundColor(Color.parseColor("#78C850"))
                tvDetailsDefenseStatLabel.setBackgroundColor(Color.parseColor("#78C850"))
                tvDetailsSpecialAttackStatLabel.setBackgroundColor(Color.parseColor("#78C850"))
                tvDetailsSpecialDefenseStatLabel.setBackgroundColor(Color.parseColor("#78C850"))
                tvDetailsSpeedStatLabel.setBackgroundColor(Color.parseColor("#78C850"))
                tvDetailsBaseStatsTotal.setTextColor(Color.parseColor("#78C850"))

                tvMoveMethod1.setBackgroundColor(Color.parseColor("#78C850"))
                tvMoveMethod2.setBackgroundColor(Color.parseColor("#78C850"))
                tvMoveMethod3.setBackgroundColor(Color.parseColor("#78C850"))
                tvMoveMethod4.setBackgroundColor(Color.parseColor("#78C850"))
            }

            "ghost" -> {
                tvDetailsAbilitiesSlot1.setBackgroundColor(Color.parseColor("#705898"))
                tvDetailsAbilitiesSlot2.setBackgroundColor(Color.parseColor("#705898"))
                tvDetailsAbilitiesSlotHidden.setBackgroundColor(Color.parseColor("#705898"))

                tvDetailsHpStatLabel.setBackgroundColor(Color.parseColor("#705898"))
                tvDetailsAttackStatLabel.setBackgroundColor(Color.parseColor("#705898"))
                tvDetailsDefenseStatLabel.setBackgroundColor(Color.parseColor("#705898"))
                tvDetailsSpecialAttackStatLabel.setBackgroundColor(Color.parseColor("#705898"))
                tvDetailsSpecialDefenseStatLabel.setBackgroundColor(Color.parseColor("#705898"))
                tvDetailsSpeedStatLabel.setBackgroundColor(Color.parseColor("#705898"))
                tvDetailsBaseStatsTotal.setTextColor(Color.parseColor("#705898"))

                tvMoveMethod1.setBackgroundColor(Color.parseColor("#705898"))
                tvMoveMethod2.setBackgroundColor(Color.parseColor("#705898"))
                tvMoveMethod3.setBackgroundColor(Color.parseColor("#705898"))
                tvMoveMethod4.setBackgroundColor(Color.parseColor("#705898"))
            }

            "ground" -> {
                tvDetailsAbilitiesSlot1.setBackgroundColor(Color.parseColor("#E0C068"))
                tvDetailsAbilitiesSlot2.setBackgroundColor(Color.parseColor("#E0C068"))
                tvDetailsAbilitiesSlotHidden.setBackgroundColor(Color.parseColor("#E0C068"))

                tvDetailsHpStatLabel.setBackgroundColor(Color.parseColor("#E0C068"))
                tvDetailsAttackStatLabel.setBackgroundColor(Color.parseColor("#E0C068"))
                tvDetailsDefenseStatLabel.setBackgroundColor(Color.parseColor("#E0C068"))
                tvDetailsSpecialAttackStatLabel.setBackgroundColor(Color.parseColor("#E0C068"))
                tvDetailsSpecialDefenseStatLabel.setBackgroundColor(Color.parseColor("#E0C068"))
                tvDetailsSpeedStatLabel.setBackgroundColor(Color.parseColor("#E0C068"))
                tvDetailsBaseStatsTotal.setTextColor(Color.parseColor("#E0C068"))

                tvMoveMethod1.setBackgroundColor(Color.parseColor("#E0C068"))
                tvMoveMethod2.setBackgroundColor(Color.parseColor("#E0C068"))
                tvMoveMethod3.setBackgroundColor(Color.parseColor("#E0C068"))
                tvMoveMethod4.setBackgroundColor(Color.parseColor("#E0C068"))
            }

            "ice" -> {
                tvDetailsAbilitiesSlot1.setBackgroundColor(Color.parseColor("#98D8D8"))
                tvDetailsAbilitiesSlot2.setBackgroundColor(Color.parseColor("#98D8D8"))
                tvDetailsAbilitiesSlotHidden.setBackgroundColor(Color.parseColor("#98D8D8"))

                tvDetailsHpStatLabel.setBackgroundColor(Color.parseColor("#98D8D8"))
                tvDetailsAttackStatLabel.setBackgroundColor(Color.parseColor("#98D8D8"))
                tvDetailsDefenseStatLabel.setBackgroundColor(Color.parseColor("#98D8D8"))
                tvDetailsSpecialAttackStatLabel.setBackgroundColor(Color.parseColor("#98D8D8"))
                tvDetailsSpecialDefenseStatLabel.setBackgroundColor(Color.parseColor("#98D8D8"))
                tvDetailsSpeedStatLabel.setBackgroundColor(Color.parseColor("#98D8D8"))
                tvDetailsBaseStatsTotal.setTextColor(Color.parseColor("#98D8D8"))

                tvMoveMethod1.setBackgroundColor(Color.parseColor("#98D8D8"))
                tvMoveMethod2.setBackgroundColor(Color.parseColor("#98D8D8"))
                tvMoveMethod3.setBackgroundColor(Color.parseColor("#98D8D8"))
                tvMoveMethod4.setBackgroundColor(Color.parseColor("#98D8D8"))
            }

            "normal" -> {
                tvDetailsAbilitiesSlot1.setBackgroundColor(Color.parseColor("#A8A878"))
                tvDetailsAbilitiesSlot2.setBackgroundColor(Color.parseColor("#A8A878"))
                tvDetailsAbilitiesSlotHidden.setBackgroundColor(Color.parseColor("#A8A878"))

                tvDetailsHpStatLabel.setBackgroundColor(Color.parseColor("#A8A878"))
                tvDetailsAttackStatLabel.setBackgroundColor(Color.parseColor("#A8A878"))
                tvDetailsDefenseStatLabel.setBackgroundColor(Color.parseColor("#A8A878"))
                tvDetailsSpecialAttackStatLabel.setBackgroundColor(Color.parseColor("#A8A878"))
                tvDetailsSpecialDefenseStatLabel.setBackgroundColor(Color.parseColor("#A8A878"))
                tvDetailsSpeedStatLabel.setBackgroundColor(Color.parseColor("#A8A878"))
                tvDetailsBaseStatsTotal.setTextColor(Color.parseColor("#A8A878"))

                tvMoveMethod1.setBackgroundColor(Color.parseColor("#A8A878"))
                tvMoveMethod2.setBackgroundColor(Color.parseColor("#A8A878"))
                tvMoveMethod3.setBackgroundColor(Color.parseColor("#A8A878"))
                tvMoveMethod4.setBackgroundColor(Color.parseColor("#A8A878"))
            }

            "poison" -> {
                tvDetailsAbilitiesSlot1.setBackgroundColor(Color.parseColor("#A040A0"))
                tvDetailsAbilitiesSlot2.setBackgroundColor(Color.parseColor("#A040A0"))
                tvDetailsAbilitiesSlotHidden.setBackgroundColor(Color.parseColor("#A040A0"))

                tvDetailsHpStatLabel.setBackgroundColor(Color.parseColor("#A040A0"))
                tvDetailsAttackStatLabel.setBackgroundColor(Color.parseColor("#A040A0"))
                tvDetailsDefenseStatLabel.setBackgroundColor(Color.parseColor("#A040A0"))
                tvDetailsSpecialAttackStatLabel.setBackgroundColor(Color.parseColor("#A040A0"))
                tvDetailsSpecialDefenseStatLabel.setBackgroundColor(Color.parseColor("#A040A0"))
                tvDetailsSpeedStatLabel.setBackgroundColor(Color.parseColor("#A040A0"))
                tvDetailsBaseStatsTotal.setTextColor(Color.parseColor("#A040A0"))

                tvMoveMethod1.setBackgroundColor(Color.parseColor("#A040A0"))
                tvMoveMethod2.setBackgroundColor(Color.parseColor("#A040A0"))
                tvMoveMethod3.setBackgroundColor(Color.parseColor("#A040A0"))
                tvMoveMethod4.setBackgroundColor(Color.parseColor("#A040A0"))
            }

            "water" -> {
                tvDetailsAbilitiesSlot1.setBackgroundColor(Color.parseColor("#6890F0"))
                tvDetailsAbilitiesSlot2.setBackgroundColor(Color.parseColor("#6890F0"))
                tvDetailsAbilitiesSlotHidden.setBackgroundColor(Color.parseColor("#6890F0"))

                tvDetailsHpStatLabel.setBackgroundColor(Color.parseColor("#6890F0"))
                tvDetailsAttackStatLabel.setBackgroundColor(Color.parseColor("#6890F0"))
                tvDetailsDefenseStatLabel.setBackgroundColor(Color.parseColor("#6890F0"))
                tvDetailsSpecialAttackStatLabel.setBackgroundColor(Color.parseColor("#6890F0"))
                tvDetailsSpecialDefenseStatLabel.setBackgroundColor(Color.parseColor("#6890F0"))
                tvDetailsSpeedStatLabel.setBackgroundColor(Color.parseColor("#6890F0"))
                tvDetailsBaseStatsTotal.setTextColor(Color.parseColor("#6890F0"))

                tvMoveMethod1.setBackgroundColor(Color.parseColor("#6890F0"))
                tvMoveMethod2.setBackgroundColor(Color.parseColor("#6890F0"))
                tvMoveMethod3.setBackgroundColor(Color.parseColor("#6890F0"))
                tvMoveMethod4.setBackgroundColor(Color.parseColor("#6890F0"))
            }

            "psychic" -> {
                tvDetailsAbilitiesSlot1.setBackgroundColor(Color.parseColor("#F85888"))
                tvDetailsAbilitiesSlot2.setBackgroundColor(Color.parseColor("#F85888"))
                tvDetailsAbilitiesSlotHidden.setBackgroundColor(Color.parseColor("#F85888"))

                tvDetailsHpStatLabel.setBackgroundColor(Color.parseColor("#F85888"))
                tvDetailsAttackStatLabel.setBackgroundColor(Color.parseColor("#F85888"))
                tvDetailsDefenseStatLabel.setBackgroundColor(Color.parseColor("#F85888"))
                tvDetailsSpecialAttackStatLabel.setBackgroundColor(Color.parseColor("#F85888"))
                tvDetailsSpecialDefenseStatLabel.setBackgroundColor(Color.parseColor("#F85888"))
                tvDetailsSpeedStatLabel.setBackgroundColor(Color.parseColor("#F85888"))
                tvDetailsBaseStatsTotal.setTextColor(Color.parseColor("#F85888"))

                tvMoveMethod1.setBackgroundColor(Color.parseColor("#F85888"))
                tvMoveMethod2.setBackgroundColor(Color.parseColor("#F85888"))
                tvMoveMethod3.setBackgroundColor(Color.parseColor("#F85888"))
                tvMoveMethod4.setBackgroundColor(Color.parseColor("#F85888"))
            }

            "rock" -> {
                tvDetailsAbilitiesSlot1.setBackgroundColor(Color.parseColor("#B8A038"))
                tvDetailsAbilitiesSlot2.setBackgroundColor(Color.parseColor("#B8A038"))
                tvDetailsAbilitiesSlotHidden.setBackgroundColor(Color.parseColor("#B8A038"))

                tvDetailsHpStatLabel.setBackgroundColor(Color.parseColor("#B8A038"))
                tvDetailsAttackStatLabel.setBackgroundColor(Color.parseColor("#B8A038"))
                tvDetailsDefenseStatLabel.setBackgroundColor(Color.parseColor("#B8A038"))
                tvDetailsSpecialAttackStatLabel.setBackgroundColor(Color.parseColor("#B8A038"))
                tvDetailsSpecialDefenseStatLabel.setBackgroundColor(Color.parseColor("#B8A038"))
                tvDetailsSpeedStatLabel.setBackgroundColor(Color.parseColor("#B8A038"))
                tvDetailsBaseStatsTotal.setTextColor(Color.parseColor("#B8A038"))

                tvMoveMethod1.setBackgroundColor(Color.parseColor("#B8A038"))
                tvMoveMethod2.setBackgroundColor(Color.parseColor("#B8A038"))
                tvMoveMethod3.setBackgroundColor(Color.parseColor("#B8A038"))
                tvMoveMethod4.setBackgroundColor(Color.parseColor("#B8A038"))
            }

            "steel" -> {
                tvDetailsAbilitiesSlot1.setBackgroundColor(Color.parseColor("#B8B8D0"))
                tvDetailsAbilitiesSlot2.setBackgroundColor(Color.parseColor("#B8B8D0"))
                tvDetailsAbilitiesSlotHidden.setBackgroundColor(Color.parseColor("#B8B8D0"))

                tvDetailsHpStatLabel.setBackgroundColor(Color.parseColor("#B8B8D0"))
                tvDetailsAttackStatLabel.setBackgroundColor(Color.parseColor("#B8B8D0"))
                tvDetailsDefenseStatLabel.setBackgroundColor(Color.parseColor("#B8B8D0"))
                tvDetailsSpecialAttackStatLabel.setBackgroundColor(Color.parseColor("#B8B8D0"))
                tvDetailsSpecialDefenseStatLabel.setBackgroundColor(Color.parseColor("#B8B8D0"))
                tvDetailsSpeedStatLabel.setBackgroundColor(Color.parseColor("#B8B8D0"))
                tvDetailsBaseStatsTotal.setTextColor(Color.parseColor("#B8B8D0"))

                tvMoveMethod1.setBackgroundColor(Color.parseColor("#B8B8D0"))
                tvMoveMethod2.setBackgroundColor(Color.parseColor("#B8B8D0"))
                tvMoveMethod3.setBackgroundColor(Color.parseColor("#B8B8D0"))
                tvMoveMethod4.setBackgroundColor(Color.parseColor("#B8B8D0"))
            }
        }
    }

    private fun setupMoveRecyclerView() {
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        movesRecyclerView.layoutManager = layoutManager

        movesRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (!recyclerView.canScrollVertically(1)) {

                }
            }
        })

        movesRecyclerView.adapter = moveAdapter
    }

    /**
     * Download the Pokémons learnable moves.
     *
     * @param url URL to download from.
     */
    private fun downloadMoves(url: String) {
        val jor = JsonObjectRequest(Request.Method.GET, url, null,
            Response.Listener {
                val resp = Gson().fromJson(it.toString(), Move::class.java)

                moves.add(resp)
                moves.sortedWith(compareBy({it.name}))

                moveAdapter.notifyDataSetChanged()
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

    private fun setupWeaknesses() {
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        weaknessesRecyclerView.layoutManager = layoutManager
        weaknessesRecyclerView.adapter = weaknessesAdapter

        for (type in pokemon.types) {
            downloadWeaknesses(type.type.url)
        }
    }

    private fun downloadWeaknesses(url: String) {
        val jor = JsonObjectRequest(Request.Method.GET, url, null,
            Response.Listener {
                val resp = Gson().fromJson(it.toString(), Type::class.java)

                for (element in resp.damageRelations.doubleDamageFrom) {
                    var exists = false

                    for (weakness in weaknesses) {
                        if (weakness == element.name)
                            exists = true
                    }

                    if (!exists)
                        weaknesses.add(element.name)
                }

                weaknessesAdapter.notifyDataSetChanged()
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

    private fun setupResistances() {
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        resistancesRecyclerView.layoutManager = layoutManager
        resistancesRecyclerView.adapter = resistancesAdapter

        for (type in pokemon.types) {
            downloadResistances(type.type.url)
        }
    }

    private fun downloadResistances(url: String) {
        val jor = JsonObjectRequest(Request.Method.GET, url, null,
            Response.Listener {
                val resp = Gson().fromJson(it.toString(), Type::class.java)

                for (element in resp.damageRelations.halfDamageFrom) {
                    var exists = false

                    for (resistance in resistances) {
                        if (resistance == element.name)
                            exists = true
                    }

                    if (!exists)
                        resistances.add(element.name)
                }

                for (element in resp.damageRelations.noDamageFrom) {
                    var exists = false

                    for (resistance in resistances) {
                        if (resistance == element.name)
                            exists = true
                    }

                    if (!exists)
                        resistances.add(element.name)
                }

                resistancesAdapter.notifyDataSetChanged()
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
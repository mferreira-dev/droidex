package pt.mferreira.droidex

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_results_sheet.*
import pt.mferreira.droidex.adapters.DexAdapter
import pt.mferreira.droidex.models.pokemon.Pokemon
import pt.mferreira.droidex.singletons.VolleySingleton

class ResultsSheetFragment : BottomSheetDialogFragment() {
    private var resultPokemon: MutableList<Pokemon> = ArrayList()
    private var resultsAdapter = DexAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style. AppBottomSheetDialogTheme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_results_sheet, container, false)
        return v
    }

    override fun onStart() {
        super.onStart()

        resultsAdapter = DexAdapter(requireContext(), resultPokemon)

        val bundle = requireArguments()
        val query = bundle.getString("query")

        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        resultsRecyclerView.layoutManager = layoutManager
        resultsRecyclerView.adapter = resultsAdapter

        search("https://pokeapi.co/api/v2/pokemon/$query")
    }

    private fun search(url: String) {
        val jor = JsonObjectRequest(
            Request.Method.GET, url, null,
            Response.Listener {
                val resp = Gson().fromJson(it.toString(), Pokemon::class.java)

                // Prevent duplicates.
                var exists = false
                if (resultPokemon.size > 0) {
                    for (p in resultPokemon) {
                        if (resp.name == p.name)
                            exists = true
                    }
                }

                if (!exists)
                    resultPokemon.add(resp)

                // Entry order depends on network fluctuation. Fix it with a bubble sort.
                var temp: Pokemon
                for (i in 0 until resultPokemon.size - 1) {
                    for (j in 0 until resultPokemon.size - 1) {
                        if (resultPokemon[j].id > resultPokemon[j + 1].id) {
                            temp = resultPokemon[j]
                            resultPokemon[j] = resultPokemon[j + 1]
                            resultPokemon[j + 1] = temp
                        }
                    }
                }

                resultsAdapter.notifyDataSetChanged()
            },
            Response.ErrorListener {
                Toast.makeText(context,
                    "Error while performing request. Please check your connection or try again later.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )

        VolleySingleton.getInstance(requireContext()).addToRequestQueue(jor)
    }
}
package pt.mferreira.droidex

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_dex.*
import pt.mferreira.droidex.adapters.DexAdapter
import pt.mferreira.droidex.models.global.PokemonPage
import pt.mferreira.droidex.models.pokemon.Pokemon
import pt.mferreira.droidex.singletons.VolleySingleton

class DexActivity : AppCompatActivity() {
    private lateinit var context: Context
    private var pokemon: MutableList<Pokemon> = ArrayList()
    private val dexAdapter = DexAdapter(this, pokemon)
    private var isLoading = true
    private var next = ""

    /**
     * Perform download operations asynchronously.
     *
     * @param params Index 0 of params contains the URL.
     */
    inner class DownloadData : AsyncTask<String, Void, Int>() {
        override fun doInBackground(vararg params: String): Int {
            if (params[0].isNotEmpty())
                downloadPage(params[0])

            return 1
        }

        override fun onPostExecute(result: Int?) {
            super.onPostExecute(result)
            isLoading = false
            changeLoadState(false)
        }
    }

    /**
     * Download new page of Pokémon (X entries).
     * This does not download data from the Pokémon themselves, only their URLs.
     *
     * @param url URL to download from.
     */
    private fun downloadPage(url: String) {
        val jor = JsonObjectRequest(Request.Method.GET, url, null,
            Response.Listener {
                val resp = Gson().fromJson(it.toString(), PokemonPage::class.java)
                next = resp.next

                for (pokemon in resp.results)
                    downloadPokemonData(pokemon.url)
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
     * Download a specific Pokémon's details and add it to the RecyclerView's adapter.
     *
     * @param url URL to download from.
     */
    private fun downloadPokemonData(url: String) {
        val jor = JsonObjectRequest(Request.Method.GET, url, null,
            Response.Listener {
                val resp = Gson().fromJson(it.toString(), Pokemon::class.java)

                // Prevent duplicates.
                var exists = false
                if (pokemon.size > 0) {
                    for (p in pokemon) {
                        if (resp.name == p.name)
                            exists = true
                    }
                }

                if (!exists)
                    pokemon.add(resp)

                // Entry order depends on network fluctuation. Fix it with a bubble sort.
                var temp: Pokemon
                for (i in 0 until pokemon.size - 1) {
                    for (j in 0 until pokemon.size - 1) {
                        if (pokemon[j].id > pokemon[j + 1].id) {
                            temp = pokemon[j]
                            pokemon[j] = pokemon[j + 1]
                            pokemon[j + 1] = temp
                        }
                    }
                }

                dexAdapter.notifyDataSetChanged()
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

    private fun setupRecyclerView() {
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        dexRecyclerView.layoutManager = layoutManager

        dexRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (!recyclerView.canScrollVertically(1)) {
                    if (!isLoading && next != null) {
                        isLoading = true
                        DownloadData().execute(next)
                    }
                }
            }
        })

        dexRecyclerView.adapter = dexAdapter
    }

    /**
     * Change progress bar visibility.
     *
     * @param state If false, the progress bar is now invisible, signifying download complete.
     */
    private fun changeLoadState (state: Boolean) {
        if (state) dexRecyclerViewProgress.visibility = View.VISIBLE
        else dexRecyclerViewProgress.visibility = View.INVISIBLE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dex)
        context = this
        changeLoadState(true)

        isLoading = true
        setupRecyclerView()
        DownloadData().execute("https://pokeapi.co/api/v2/pokemon?limit=150")
    }
}
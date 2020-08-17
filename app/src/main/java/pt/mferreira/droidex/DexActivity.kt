package pt.mferreira.droidex

import android.content.Context
import android.database.Cursor
import android.database.MatrixCursor
import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.provider.BaseColumns
import android.view.Menu
import android.view.View
import android.view.Window
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.cursoradapter.widget.CursorAdapter
import androidx.cursoradapter.widget.SimpleCursorAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_dex.*
import kotlinx.android.synthetic.main.fragment_results_sheet.*
import pt.mferreira.droidex.adapters.DexAdapter
import pt.mferreira.droidex.models.global.PokemonPage
import pt.mferreira.droidex.models.pokemon.Pokemon
import pt.mferreira.droidex.singletons.VolleySingleton
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStreamReader

class DexActivity : AppCompatActivity() {
    private lateinit var context: Context
    private var pokemon: MutableList<Pokemon> = ArrayList()
    private val dexAdapter = DexAdapter(this, pokemon)
    private var isLoading = true
    private var next = ""
    private lateinit var suggestionsAdapter: SimpleCursorAdapter
    private var history = arrayListOf<String>()
    private lateinit var searchView: SearchView

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

        val window: Window = window
        window.statusBarColor = Color.parseColor("#E53935")
        setSupportActionBar(toolbar)

        isLoading = true
        setupRecyclerView()
        DownloadData().execute("https://pokeapi.co/api/v2/pokemon?limit=100")
    }

    /**
     * Load input history from file.
     */
    private fun loadHistory() {
        history = arrayListOf()

        try {
            val fis: FileInputStream = openFileInput("history.txt")
            val isr = InputStreamReader(fis)
            val br = BufferedReader(isr)
            var line = br.readLine()

            while (line != null) {
                history.add(line)
                line = br.readLine()
            }

            isr.close()
            fis.close()
        } catch (ex: Exception) {
            // File does not exist yet.
        }

        history.reverse()

        val from = arrayOf("string")
        val to = intArrayOf(android.R.id.text1)
        suggestionsAdapter = SimpleCursorAdapter(context, R.layout.suggestion_item, null, from, to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER)

        updateSuggestionsAdapter("")
        suggestionsAdapter.notifyDataSetChanged()
        searchView.suggestionsAdapter = suggestionsAdapter
    }

    /**
     * Update suggestions as user modifies the field.
     *
     * @param s New character.
     */
    private fun updateSuggestionsAdapter(s: String) {
        val c = MatrixCursor(arrayOf(BaseColumns._ID, "string"))
        val list = mutableListOf<String>()

        if (s.isNotEmpty()) {
            for (entry in history) if (entry.contains(s)) list.add(entry)
        } else {
            for (entry in history) list.add(entry)
        }

        for (i in 0 until list.size) c.addRow(arrayOf(i, list[i]))
        suggestionsAdapter.changeCursor(c)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.options_menu, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        super.onPrepareOptionsMenu(menu)

        val searchItem = menu.findItem(R.id.action_search)
        searchView = searchItem?.actionView as SearchView

        val autoCompleteTextViewID = resources.getIdentifier("search_src_text", "id", packageName)
        val searchAutoCompleteTextView = searchView.findViewById(autoCompleteTextViewID) as? AutoCompleteTextView
        searchAutoCompleteTextView?.threshold = 0

        val dropDownAnchor: View = searchView.findViewById(searchAutoCompleteTextView!!.dropDownAnchor);
        dropDownAnchor.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom -> // screen width
            val screenWidthPixel: Int = resources.displayMetrics.widthPixels
            searchAutoCompleteTextView.dropDownWidth = screenWidthPixel
        }

        loadHistory()

        searchView.setOnCloseListener(object: SearchView.OnCloseListener {
            override fun onClose(): Boolean {
                if (pokemon.size > 1) {
                    pokemon.clear()
                    DownloadData().execute("https://pokeapi.co/api/v2/pokemon?limit=100")
                }

                return true
            }
        })

        searchView.setOnSuggestionListener(object: SearchView.OnSuggestionListener {
            override fun onSuggestionClick(position: Int): Boolean {
                val cursor = suggestionsAdapter.getItem(position) as Cursor
                val txt = cursor.getString(cursor.getColumnIndex("string"))
                searchView.setQuery(txt, true)

                return true
            }

            override fun onSuggestionSelect(position: Int): Boolean {
                return true
            }
        })

        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(s: String): Boolean {
                val fos: FileOutputStream = openFileOutput("history.txt", MODE_APPEND)
                fos.write("$s\n".toByteArray())
                fos.close()
                loadHistory()

                val bundle = Bundle()
                bundle.putString("query", s)

                val resultsSheetFragment = ResultsSheetFragment()
                resultsSheetFragment.arguments = bundle
                resultsSheetFragment.show(supportFragmentManager, resultsSheetFragment.tag)

                return false
            }

            override fun onQueryTextChange(s: String): Boolean {
                updateSuggestionsAdapter(s)
                return false
            }
        })

        return true
    }
}
package pt.mferreira.droidex

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_ability_sheet.*
import pt.mferreira.droidex.models.ability.Ability
import pt.mferreira.droidex.singletons.VolleySingleton

class AbilitySheetFragment : BottomSheetDialogFragment() {
    private lateinit var url: String
    private lateinit var ability: Ability

    /**
     * Download the ability's info.
     *
     * @param url URL to download from.
     */
    private fun downloadInfo(url: String) {
        val jor = JsonObjectRequest(
            Request.Method.GET, url, null,
            Response.Listener {
                val ability = Gson().fromJson(it.toString(), Ability::class.java)

                // Ability name.
                if (ability.name.contains("-")) {
                    tvDetailsSheetAbility.text = formatName(ability.name)
                } else
                    tvDetailsSheetAbility.text = "${ability.name.substring(0, 1).toUpperCase()}${ability.name.substring(1)}"

                // Find latest english game description.
                var flavorText = ""
                for (element in ability.gameDescription)
                    if (element.language.name == "en") flavorText = element.flavorText
                tvDetailsSheetGameDescription.text = flavorText

                // Find latest english in-depth description.
                var inDepth = ""
                for (element in ability.inDepthEffect)
                    if (element.language.name == "en") inDepth = element.effect
                tvDetailsSheetInDepth.text = inDepth
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style. AppBottomSheetDialogTheme)

        val bundle = requireArguments()
        bundle.getString("url")?.let { downloadInfo(it) }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_ability_sheet, container, false)
        return v
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
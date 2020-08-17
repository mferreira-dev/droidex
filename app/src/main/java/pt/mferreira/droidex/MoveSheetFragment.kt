package pt.mferreira.droidex

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_ability_sheet.*
import kotlinx.android.synthetic.main.fragment_move_sheet.*
import pt.mferreira.droidex.models.move.Move

class MoveSheetFragment : BottomSheetDialogFragment() {
    private lateinit var move: Move

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style. AppBottomSheetDialogTheme)

        val bundle = requireArguments()
        move = bundle.getSerializable("move") as Move
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_move_sheet, container, false)
        return v
    }

    override fun onStart() {
        super.onStart()

        tvDetailsSheetMove.text = formatName(move.name)

        // Find latest english game description.
        var str = ""
        for (element in move.flavorText)
            if (element.language.name == "en") str = element.flavorText
        tvDetailsSheetGameDescriptionMove.text = str

        // Find latest english in-depth description.
        str = ""
        for (element in move.effectEntries)
            if (element.language.name == "en") str = element.effect
        tvDetailsSheetEffectMove.text = str
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
package pt.mferreira.droidex.models.pokemon

import com.google.gson.annotations.SerializedName
import pt.mferreira.droidex.models.global.NamedApiResource

/**
 * @property stat URL for the stat represented by this object. (Stat class)
 * @property effort Number of EVs this Pokémon yields for this particular stat.
 * @property baseStat Number of EVs this Pokémon yields for this particular stat.
 */
data class PokemonStat (
    val stat: NamedApiResource,
    val effort: Int,
    @SerializedName("base_stat") val baseStat: Int
)
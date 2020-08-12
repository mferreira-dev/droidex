package pt.mferreira.droidex.models.ability

import com.google.gson.annotations.SerializedName
import pt.mferreira.droidex.models.global.NamedApiResource

/**
 * @property isHidden True if this is the Pokémon's hidden ability.
 * @property slot The slot this ability occupies in this Pokémon's species.
 * @property pokemon URL to the Pokémon. (Pokemon class)
 */
data class AbilityPokemon (
    @SerializedName("is_hidden") val isHidden: Boolean,
    val slot: Int,
    val pokemon: NamedApiResource
)
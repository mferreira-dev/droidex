package pt.mferreira.droidex.models.pokemon

import com.google.gson.annotations.SerializedName
import pt.mferreira.droidex.models.global.NamedApiResource

/**
 * @property isHidden True if this ability is the Pokémon's hidden ability.
 * @property slot The slot this ability occupies in this Pokémon's species.
 * @property ability URL to the ability represented by the property. (Ability class)
 */
data class PokemonAbility (
    @SerializedName("is_hidden") val isHidden: Boolean,
    val slot: Int,
    val ability: NamedApiResource
)
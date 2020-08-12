package pt.mferreira.droidex.models.pokemon

import com.google.gson.annotations.SerializedName
import pt.mferreira.droidex.models.global.NamedApiResource

/**
 * @property move URL to the move represented by this object. (Move class)
 * @property details The move's details for this specific Pokémon (e.g. learn method).
 */
data class PokemonMove (
    val move: NamedApiResource,
    @SerializedName("version_group_details") val details: List<PokemonMoveVersion>
)
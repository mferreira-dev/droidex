package pt.mferreira.droidex.models.ability

import com.google.gson.annotations.SerializedName
import pt.mferreira.droidex.models.global.NamedApiResource
import pt.mferreira.droidex.models.global.VerboseEffect

/**
 * @property id The identifier for this resource.
 * @property name The ability's name.
 * @property isMainSeries True if this ability originated in a main series game.
 * @property generation URL to the generation in which this ability originated. (Generation class)
 * @property inDepthEffect In-depth description of the ability's effect.
 * @property gameDescription In-game description of the ability's effect.
 * @property pokemon List of Pokémon that can learn this ability.
 */
data class Ability (
    val id: Int,
    val name: String,
    @SerializedName("is_main_series") val isMainSeries: Boolean,
    val generation: NamedApiResource,
    @SerializedName("effect_entries") val inDepthEffect: VerboseEffect,
    @SerializedName("flavor_text_entries") val gameDescription: AbilityFlavorText,
    val pokemon: List<AbilityPokemon>
)
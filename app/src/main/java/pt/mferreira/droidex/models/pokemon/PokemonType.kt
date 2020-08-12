package pt.mferreira.droidex.models.pokemon

import pt.mferreira.droidex.models.global.NamedApiResource

/**
 * @property slot Which of the Pokémon's slot this type belongs to (1 or 2).
 * @property type URL to the type represented by this object. (Type class)
 */
data class PokemonType (
    val slot: Int,
    val type: NamedApiResource
)
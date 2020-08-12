package pt.mferreira.droidex.models.type

import pt.mferreira.droidex.models.global.NamedApiResource

/**
 * @property slot The Pokémon's slot this type belongs to. (1 or 2)
 * @property pokemon URL of the Pokémon this object represents. (Pokemon class)
 */
data class TypePokemon (
    val slot: Int,
    val pokemon: NamedApiResource
)
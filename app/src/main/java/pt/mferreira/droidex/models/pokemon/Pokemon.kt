package pt.mferreira.droidex.models.pokemon

import pt.mferreira.droidex.models.global.NamedApiResource
import java.io.Serializable

/**
 * @property id The identifier for this resource.
 * @property name The Pokémon's name.
 * @property height The Pokémon's height.
 * @property weight The Pokémon's weight.
 * @property types List of the Pokémon's type.
 * @property abilities List of the abilities the Pokémon can possess.
 * @property stats List of the Pokémon's base stats.
 * @property moves List of the moves the Pokémon can learn.
 * @property sprites URLs of the Pokémon's sprites.
 * @property species URL of the Pokémon's species. (PokemonSpecies class)
 */
data class Pokemon (
    var id: Int,
    val name: String,
    val height: Int,
    val weight: Int,
    val types: List<PokemonType>,
    val abilities: List<PokemonAbility>,
    val stats: List<PokemonStat>,
    val moves: List<PokemonMove>,
    val sprites: PokemonSprites,
    val species: NamedApiResource
) : Serializable
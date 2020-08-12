package pt.mferreira.droidex.models.type

import com.google.gson.annotations.SerializedName
import pt.mferreira.droidex.models.global.NamedApiResource

/**
 * @property id The identifier for this resource.
 * @property name The type's name.
 * @property typeRelations Details on how effective this type is toward others and vice versa.
 * @property generation URL to the generation in which this type was introduced in. (Generation class)
 * @property moves List of URLs of moves belonging to this type. (Move class)
 * @property pokemon List of Pokémon that have this type.
 */
data class Type (
    val id: Int,
    val name: String,
    @SerializedName("damage_relations") val typeRelations: TypeRelations,
    val generation: NamedApiResource,
    val moves: List<NamedApiResource>,
    val pokemon: List<TypePokemon>
)
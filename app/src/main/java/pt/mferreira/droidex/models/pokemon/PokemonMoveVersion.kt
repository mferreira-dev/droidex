package pt.mferreira.droidex.models.pokemon

import com.google.gson.annotations.SerializedName
import pt.mferreira.droidex.models.global.NamedApiResource
import java.io.Serializable

/**
 * @property learnMethod URL to the learn method represented by this object. (MoveLearnMethod class)
 * @property levelLearnedAt At what level this Pokémon learns this move.
 */
data class PokemonMoveVersion (
    @SerializedName("move_learn_method") val learnMethod: NamedApiResource,
    @SerializedName("level_learned_at") val levelLearnedAt: Int
) : Serializable
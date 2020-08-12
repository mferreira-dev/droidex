package pt.mferreira.droidex.models.stat

import com.google.gson.annotations.SerializedName

/**
 * @property id The identifier for this resource.
 * @property name The stat's name.
 * @property gameIndex ID the games use for this stat.
 * @property isBattleOnly True if this stat only exists within a battle.
 */
data class Stat (
    val id: Int,
    val name: String,
    @SerializedName("game_index") val gameIndex: Int,
    @SerializedName("is_battle_only") val isBattleOnly: Boolean
)
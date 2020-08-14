package pt.mferreira.droidex.models.move

import com.google.gson.annotations.SerializedName
import pt.mferreira.droidex.models.global.NamedApiResource
import pt.mferreira.droidex.models.global.VerboseEffect

data class Move (
    val id: Int,
    val name: String,
    val accuracy: Int,
    @SerializedName("effect_chance") val effectChange: Int,
    val pp: Int,
    val priority: Int,
    val power: Int,
    @SerializedName("damage_class") val damageClass: NamedApiResource,
    @SerializedName("effect_entries") val effectEntries: List<VerboseEffect>,
    @SerializedName("flavor_text_entries") val flavorText: List<MoveFlavorText>,
    val type: NamedApiResource
)
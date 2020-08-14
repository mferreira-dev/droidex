package pt.mferreira.droidex.models.ability

import com.google.gson.annotations.SerializedName
import pt.mferreira.droidex.models.global.NamedApiResource

/**
 * @property flavorText In-game description of the ability.
 */
data class AbilityFlavorText (
    @SerializedName("flavor_text") val flavorText: String,
    val language: NamedApiResource,
    @SerializedName("version_group") val version: NamedApiResource
)
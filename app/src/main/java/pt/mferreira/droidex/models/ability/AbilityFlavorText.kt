package pt.mferreira.droidex.models.ability

import com.google.gson.annotations.SerializedName

/**
 * @property flavorText In-game description of the ability.
 */
data class AbilityFlavorText (
    @SerializedName("flavor_text") val flavorText: String
)
package pt.mferreira.droidex.models.pokemon

import com.google.gson.annotations.SerializedName
import pt.mferreira.droidex.models.global.FlavorText
import pt.mferreira.droidex.models.global.NamedApiResource
import pt.mferreira.droidex.models.species.Genus

data class PokemonSpecies (
    @SerializedName("gender_rate") val genderRate: Int,
    @SerializedName("capture_rate") val captureRate: Int,
    @SerializedName("has_gender_differences") val hasGenderDifferences: Boolean,
    @SerializedName("forms_switchable") val formsSwitchable: Boolean,
    @SerializedName("growth_rate") val growthRate: NamedApiResource,
    @SerializedName("egg_groups") val eggGroups: List<NamedApiResource>,
    val genera: List<Genus>,
    @SerializedName("flavor_text_entries") val flavorText: List<FlavorText>
)
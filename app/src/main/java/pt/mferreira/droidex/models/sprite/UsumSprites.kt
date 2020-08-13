package pt.mferreira.droidex.models.sprite

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class UsumSprites (
    @SerializedName("front_default") val frontDefault: String,
    @SerializedName("front_female") val frontFemale: String,
    @SerializedName("front_shiny") val frontShiny: String,
    @SerializedName("front_shiny_female") val frontShinyFemale: String
) : Serializable
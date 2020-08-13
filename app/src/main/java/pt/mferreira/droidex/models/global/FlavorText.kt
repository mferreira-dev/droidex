package pt.mferreira.droidex.models.global

import com.google.gson.annotations.SerializedName

data class FlavorText (
    @SerializedName("flavor_text") val flavorText: String,
    val language: NamedApiResource,
    val version: NamedApiResource
)
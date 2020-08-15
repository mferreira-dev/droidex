package pt.mferreira.droidex.models.move

import com.google.gson.annotations.SerializedName
import pt.mferreira.droidex.models.global.NamedApiResource
import java.io.Serializable

data class MoveFlavorText (
    @SerializedName("flavor_text") val flavorText: String,
    val language: NamedApiResource
) : Serializable
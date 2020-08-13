package pt.mferreira.droidex.models.sprite

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class OtherSprites (
    @SerializedName("official-artwork") val officialArtwork: OfficialArtwork
) : Serializable
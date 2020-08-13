package pt.mferreira.droidex.models.sprite

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class VersionSprites (
    @SerializedName("generation-vii") val generationVii: GenerationViiSprites
) : Serializable
package pt.mferreira.droidex.models.sprite

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class GenerationViiSprites (
    @SerializedName("ultra-sun-ultra-moon") val usum: UsumSprites
) : Serializable
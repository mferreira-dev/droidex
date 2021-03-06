package pt.mferreira.droidex.models.global

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * @property effect Full description.
 * @property shortEffect Shortened description.
 */
data class VerboseEffect (
    val effect: String,
    @SerializedName("short_effect") val shortEffect: String,
    val language: NamedApiResource
): Serializable
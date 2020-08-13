package pt.mferreira.droidex.models.global

import java.io.Serializable

data class NamedApiResource (
    val name: String,
    val url: String
) : Serializable
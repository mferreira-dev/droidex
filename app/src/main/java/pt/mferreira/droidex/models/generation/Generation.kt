package pt.mferreira.droidex.models.generation

import pt.mferreira.droidex.models.global.NamedApiResource

/**
 * @property id The identifier for this resource.
 * @property name The generation's name.
 * @property abilities List of URLs pertaining to abilities that originated in this generation.
 */
data class Generation (
    val id: Int,
    val name: String,
    val abilities: List<NamedApiResource>
)
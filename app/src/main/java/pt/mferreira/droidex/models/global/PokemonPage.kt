package pt.mferreira.droidex.models.global

data class PokemonPage (
    val results: List<NamedApiResource>,
    val count: Int,
    val previous: String,
    val next: String
)
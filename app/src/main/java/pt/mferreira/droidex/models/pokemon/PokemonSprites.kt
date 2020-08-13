package pt.mferreira.droidex.models.pokemon

import pt.mferreira.droidex.models.sprite.OtherSprites
import pt.mferreira.droidex.models.sprite.VersionSprites
import java.io.Serializable

data class PokemonSprites (
    val other: OtherSprites,
    val versions: VersionSprites
) : Serializable
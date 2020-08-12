package pt.mferreira.droidex

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import pt.mferreira.droidex.models.ability.AbilityFlavorText
import pt.mferreira.droidex.models.pokemon.Pokemon
import pt.mferreira.droidex.models.pokemon.PokemonType

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
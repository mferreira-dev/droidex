package pt.mferreira.droidex.models.type

import com.google.gson.annotations.SerializedName
import pt.mferreira.droidex.models.global.NamedApiResource

/**
 * @property noDamageTo List of URLs that represent the types this move deals x0 damage to. (Type class)
 * @property halfDamageTo List of URLs that represent the types this move deals x0.5 damage to. (Type class)
 * @property doubleDamageTo List of URLs that represent the types this move deals x2 damage to. (Type class)
 * @property noDamageFrom List of URLs that represent the types this move takes x0 damage from. (Type class)
 * @property halfDamageFrom List of URLs that represent the types this move takes x0.5 damage from. (Type class)
 * @property doubleDamageFrom List of URLs that represent the types this move takes x2 damage from. (Type class)
 */
data class DamageRelations (
    @SerializedName("no_damage_to") val noDamageTo: List<NamedApiResource>,
    @SerializedName("half_damage_to") val halfDamageTo: List<NamedApiResource>,
    @SerializedName("double_damage_to") val doubleDamageTo: List<NamedApiResource>,
    @SerializedName("no_damage_from") val noDamageFrom: List<NamedApiResource>,
    @SerializedName("half_damage_from") val halfDamageFrom: List<NamedApiResource>,
    @SerializedName("double_damage_from") val doubleDamageFrom: List<NamedApiResource>
)
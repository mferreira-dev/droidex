package pt.mferreira.droidex.models.type

import pt.mferreira.droidex.models.global.NamedApiResource

/**
 * @property noDamageTo List of URLs that represent the types this move deals x0 damage to. (Type class)
 * @property halfDamageTo List of URLs that represent the types this move deals x0.5 damage to. (Type class)
 * @property doubleDamageTo List of URLs that represent the types this move deals x2 damage to. (Type class)
 * @property noDamageFrom List of URLs that represent the types this move takes x0 damage from. (Type class)
 * @property halfDamageFrom List of URLs that represent the types this move takes x0.5 damage from. (Type class)
 * @property doubleDamageFrom List of URLs that represent the types this move takes x2 damage from. (Type class)
 */
data class TypeRelations (
    val noDamageTo: List<NamedApiResource>,
    val halfDamageTo: List<NamedApiResource>,
    val doubleDamageTo: List<NamedApiResource>,
    val noDamageFrom: List<NamedApiResource>,
    val halfDamageFrom: List<NamedApiResource>,
    val doubleDamageFrom: List<NamedApiResource>
)
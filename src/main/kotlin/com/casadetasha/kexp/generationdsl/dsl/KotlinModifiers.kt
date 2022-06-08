package com.casadetasha.kexp.generationdsl.dsl

import com.squareup.kotlinpoet.KModifier

object KotlinModifiers {

    enum class Visibility {
        PRIVATE,
        PROTECTED,
        INTERNAL,
        PUBLIC
    }

    fun Visibility.toKModifier(): KModifier = when (this) {
        Visibility.PRIVATE -> KModifier.PRIVATE
        Visibility.PROTECTED -> KModifier.PROTECTED
        Visibility.INTERNAL -> KModifier.INTERNAL
        Visibility.PUBLIC -> KModifier.PUBLIC
    }
}

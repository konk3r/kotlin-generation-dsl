package com.casadetasha.kexp.generationdsl.dsl

import com.squareup.kotlinpoet.TypeName

class SuperclassTemplate constructor(val className: TypeName,
                                             function: (SuperclassTemplate.() -> Unit)?) {

    internal val constructorParams: MutableList<CodeTemplate> = ArrayList()

    init {
        function?.let{ this.function() }
    }

    fun collectConstructorParamTemplates(function: () -> Collection<CodeTemplate>) {
        constructorParams.addAll(function())
    }

    fun generateConstructorParam(function: () -> CodeTemplate) {
        constructorParams.add(function())
    }
}

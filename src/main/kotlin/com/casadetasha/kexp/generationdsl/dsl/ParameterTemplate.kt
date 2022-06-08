package com.casadetasha.kexp.generationdsl.dsl

import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.TypeName

class ParameterTemplate(
    val name: String,
    val typeName: TypeName,
    function: (ParameterTemplate.() -> Unit)? = null
) {

    private val paramBuilder = ParameterSpec.builder(name, typeName)

    internal val parameterSpec: ParameterSpec by lazy {
        function?.let { this.function() }
        paramBuilder.build()
    }

    fun defaultValue(function: () -> CodeTemplate) {
        paramBuilder.defaultValue(function().codeBlock)
    }
}

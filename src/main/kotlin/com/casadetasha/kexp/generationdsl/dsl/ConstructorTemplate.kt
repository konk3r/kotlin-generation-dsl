package com.casadetasha.kexp.generationdsl.dsl

import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.TypeName

class ConstructorTemplate(function: ConstructorTemplate.() -> Unit) {

    private val constructorBuilder = FunSpec.constructorBuilder()
    internal val constructorSpec: FunSpec

    init {
        this.function()
        constructorSpec = constructorBuilder.build()
    }

    private fun addParameter(parameterTemplate: ParameterTemplate) {
        constructorBuilder.addParameter(parameterTemplate.parameterSpec)
    }

    private fun addParameters(parameterTemplates: Collection<ParameterTemplate>) {
        constructorBuilder.addParameters(parameterTemplates.map{ it.parameterSpec })
    }

    fun collectParameterTemplates(function: () -> Collection<ParameterTemplate>) {
        addParameters(function())
    }

    fun collectConstructorPropertyTemplates(classTemplate: ClassTemplate, function: ConstructorTemplate.() -> Collection<ConstructorPropertyTemplate>) {
        val properties = function()
        addParameters(properties.map { ParameterTemplate(name = it.name, typeName = it.typeName) })
        classTemplate.addProperties(properties)
    }

    fun generateParameterTemplate(name: String, typeName: TypeName) {
        addParameter(
            ParameterTemplate(
                name = name,
                typeName = typeName
            )
        )
    }
}

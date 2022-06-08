package com.casadetasha.kexp.generationdsl.dsl

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.asTypeName
import kotlin.reflect.KClass

abstract class KotlinContainerTemplate {

    internal abstract fun addFunction(functionTemplate: FunctionTemplate)
    internal abstract fun addProperties(properties: Collection<PropertyTemplate>)

    fun collectFunctionTemplates(function: KotlinContainerTemplate.() -> Collection<FunctionTemplate>) {
        function().forEach { addFunction(it) }
    }

    fun generateFunction(
        name: String,
        receiverType: ClassName? = null,
        returnType: ClassName? = null,
        function: FunctionTemplate.() -> Unit
    ) {
        addFunction(FunctionTemplate(name, receiverType, returnType, function))
    }

    fun collectPropertyTemplates(function: KotlinContainerTemplate.() -> Collection<PropertyTemplate>) {
        addProperties(function())
    }

    fun generateProperty(
        name: String,
        type: KClass<*>,
        isMutable: Boolean? = null,
        annotations: Collection<AnnotationTemplate>? = null,
        function: PropertyTemplate.() -> Unit) {

        val template = PropertyTemplate(
            name = name,
            typeName = type.asTypeName(),
            isMutable = isMutable,
            annotations = annotations,
            function = function
        )

        addProperties(listOf(template))
    }
}


package com.casadetasha.kexp.generationdsl.dsl

import com.casadetasha.kexp.generationdsl.dsl.KotlinModifiers.toKModifier
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asTypeName
import kotlin.reflect.KClass

open class PropertyTemplate(
    val name: String,
    val typeName: TypeName,
    isMutable: Boolean? = null,
    annotations: Collection<AnnotationTemplate>? = null,
    function: (PropertyTemplate.() -> Unit)? = null
) {

    private val propertyBuilder = PropertySpec.builder(name, typeName)

    internal val propertySpec: PropertySpec by lazy {
        annotations?.let { propertyBuilder.addAnnotations(annotations.map { it.annotationSpec } ) }
        isMutable?.let { propertyBuilder.mutable() }
        function?.let { this.function() }
        propertyBuilder.build()
    }

    fun initializer(function: () -> CodeTemplate) {
        propertyBuilder.initializer(function().codeBlock)
    }

    fun delegate(function: () -> CodeTemplate) {
        propertyBuilder.delegate(function().codeBlock)
    }

    fun override() {
        propertyBuilder.addModifiers(KModifier.OVERRIDE)
    }

    fun visibility(function: () -> KotlinModifiers.Visibility) {
        propertyBuilder.addModifiers(function().toKModifier())
    }
}

class ConstructorPropertyTemplate(
    name: String,
    typeName: TypeName,
    isMutable: Boolean? = null,
    annotations: Collection<AnnotationTemplate>? = null,
    function: (PropertyTemplate.() -> Unit)? = null
): PropertyTemplate(
    name = name,
    typeName = typeName,
    isMutable =  isMutable,
    annotations = annotations,
    function = function
) {

    init {
        initializer { CodeTemplate(name) }
    }
}

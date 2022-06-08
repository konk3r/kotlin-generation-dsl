package com.casadetasha.kexp.generationdsl.dsl

import com.squareup.kotlinpoet.*
import kotlin.reflect.KClass

sealed class BaseTypeTemplate<TYPE>(
    private val modifiers: Collection<KModifier>?,
    annotations: Collection<AnnotationTemplate>?,
    function: TYPE.() -> Unit
): KotlinContainerTemplate() {

    protected abstract val typeBuilder: TypeSpec.Builder

    internal val typeSpec: TypeSpec by lazy {
        annotations?.let {
            typeBuilder.addAnnotations(
                it.map { annotationTemplate -> annotationTemplate.annotationSpec }
            )
        }
        modifiers?.let { typeBuilder.addModifiers(modifiers) }

        // This is a sealed class, and we can see that this is valid for all child classes
        @Suppress("UNCHECKED_CAST")
        (this as TYPE).function()

        return@lazy typeBuilder.build()
    }

    override fun addFunction(functionTemplate: FunctionTemplate) {
        typeBuilder.addFunction(functionTemplate.functionSpec)
    }

    override fun addProperties(properties: Collection<PropertyTemplate>) {
        typeBuilder.addProperties(properties.map { it.propertySpec })
    }

    private fun addSuperclass(superclassTemplate: SuperclassTemplate) {
        typeBuilder.superclass(superclassTemplate.className)
        superclassTemplate.constructorParams.forEach { constructorParam ->
            typeBuilder.addSuperclassConstructorParameter(constructorParam.codeBlock)
        }
    }

    fun generateSuperClass(className: KClass<*>, function: (SuperclassTemplate.() -> Unit)? = null) {
        addSuperclass(SuperclassTemplate(className = className.asTypeName(), function = function))
    }

    fun generateSuperClass(className: TypeName, function: (SuperclassTemplate.() -> Unit)? = null) {
        addSuperclass(SuperclassTemplate(className = className, function = function))
    }

    fun generatePrimaryConstructor(function: ConstructorTemplate.() -> Unit) {
        typeBuilder.primaryConstructor(ConstructorTemplate(function).constructorSpec)
    }
}

open class ClassTemplate internal constructor(
    className: ClassName,
    modifiers: Collection<KModifier>?,
    annotations: Collection<AnnotationTemplate>?,
    function: ClassTemplate.() -> Unit
): BaseTypeTemplate<ClassTemplate>(
    modifiers = modifiers,
    annotations = annotations,
    function = function
) {

    override val typeBuilder = TypeSpec.classBuilder(className)

    private fun addCompanionObject(companionObjectTemplate: CompanionObjectTemplate) {
        typeBuilder.addType(companionObjectTemplate.typeSpec)
    }

    fun generateCompanionObject(
        modifiers: Collection<KModifier>? = null,
        annotations: Collection<AnnotationTemplate>? = null,
        function: CompanionObjectTemplate.() -> Unit,
    ) {
        addCompanionObject(CompanionObjectTemplate(modifiers, annotations, function))
    }
}

class ObjectTemplate internal constructor(
    className: ClassName,
    modifiers: Collection<KModifier>?,
    annotations: Collection<AnnotationTemplate>?,
    function: ClassTemplate.() -> Unit
): ClassTemplate(
    className = className,
    modifiers = modifiers,
    annotations = annotations,
    function = function
) {

    override val typeBuilder = TypeSpec.objectBuilder(className)
}

class CompanionObjectTemplate internal constructor(
    modifiers: Collection<KModifier>?,
    annotations: Collection<AnnotationTemplate>?,
    function: CompanionObjectTemplate.() -> Unit
): BaseTypeTemplate<CompanionObjectTemplate>(
    modifiers = modifiers,
    annotations = annotations,
    function = function
) {

    override val typeBuilder = TypeSpec.companionObjectBuilder()
}

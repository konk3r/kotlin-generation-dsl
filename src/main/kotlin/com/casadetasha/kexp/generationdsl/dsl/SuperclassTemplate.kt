package com.casadetasha.kexp.generationdsl.dsl

import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asTypeName
import kotlin.reflect.KClass

class SuperclassTemplate private constructor(val className: TypeName,
                                             function: (SuperclassTemplate.() -> Unit)?) {

    internal val constructorParams: MutableList<CodeTemplate> = ArrayList()

    init {
        function?.let{ this.function() }
    }

    companion object {

        fun SuperclassTemplate.collectConstructorParamTemplates(function: () -> Collection<CodeTemplate>) {
            constructorParams.addAll(function())
        }

        fun SuperclassTemplate.generateConstructorParam(function: () -> CodeTemplate) {
            constructorParams.add(function())
        }

        fun BaseTypeTemplate<*>.generateSuperClass(className: KClass<*>,
                                                   function: (SuperclassTemplate.() -> Unit)? = null) {
            addSuperclass(SuperclassTemplate(className = className.asTypeName(), function = function))
        }

        fun BaseTypeTemplate<*>.generateSuperClass(className: TypeName,
                                                   function: (SuperclassTemplate.() -> Unit)? = null) {
            addSuperclass(SuperclassTemplate(className = className, function = function))
        }
    }
}

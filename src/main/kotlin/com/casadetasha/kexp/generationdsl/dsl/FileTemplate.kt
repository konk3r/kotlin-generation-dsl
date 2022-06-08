package com.casadetasha.kexp.generationdsl.dsl

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.KModifier
import java.io.File

class FileTemplate private constructor(
    private val directory: String,
    packageName: String,
    fileName: String,
    buildFileFunction: FileTemplate.() -> Unit): KotlinContainerTemplate() {

    private val fileBuilder = FileSpec.builder(
        packageName = packageName,
        fileName = fileName
    )

    private val fileSpec by lazy { fileBuilder.build() }

    init {
        this.buildFileFunction()
    }

    fun writeToDisk() {
        fileSpec.writeTo(File(directory))
    }

    private fun addClass(classTemplate: ClassTemplate) {
        fileBuilder.addType(classTemplate.typeSpec)
    }

    private fun addObject(objectTemplate: ObjectTemplate) {
        fileBuilder.addType(objectTemplate.typeSpec)
    }

    override fun addFunction(functionTemplate: FunctionTemplate) {
        fileBuilder.addFunction(functionTemplate.functionSpec)
    }

    override fun addProperties(properties: Collection<PropertyTemplate>) {
        properties.forEach{ fileBuilder.addProperty(it.propertySpec) }
    }

    fun generateClass(
        className: ClassName,
        modifiers: Collection<KModifier>? = null,
        annotations: Collection<AnnotationTemplate>? = null,
        function: ClassTemplate.() -> Unit,
    ) {
        addClass(ClassTemplate(className, modifiers, annotations, function))
    }

    fun generateObject(
        className: ClassName,
        modifiers: Collection<KModifier>? = null,
        annotations: Collection<AnnotationTemplate>? = null,
        function: ClassTemplate.() -> Unit,
    ) {
        addObject(ObjectTemplate(className, modifiers, annotations, function))
    }

    fun generateImport(importPackage: String, importName: String) {
        val importTemplate = ImportTemplate(importPackage, importName)
        fileBuilder.addImport(importTemplate.importPackage, importTemplate.importName)
    }

    companion object {

        fun generateFile(directory: String, packageName: String, fileName: String, buildFileFunction: FileTemplate.() -> Unit,): FileTemplate =
                FileTemplate(directory, packageName, fileName, buildFileFunction)
    }
}

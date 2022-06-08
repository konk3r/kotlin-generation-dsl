package com.casadetasha.kexp.generationdsl.dsl

import com.casadetasha.kexp.generationdsl.dsl.KotlinModifiers.toKModifier
import com.squareup.kotlinpoet.*

class FunctionTemplate(
    name: String,
    receiverType: TypeName? = null,
    returnType: TypeName? = null,
    function: FunctionTemplate.() -> Unit
) {

    private val functionBuilder = FunSpec.builder(name)
    internal val functionSpec: FunSpec

    init {
        receiverType?.let { functionBuilder.receiver(receiverType) }
        returnType?.let { functionBuilder.returns(returnType) }
        this.function()
        functionSpec = functionBuilder.build()
    }

    fun override() {
        functionBuilder.addModifiers(KModifier.OVERRIDE)
    }

    fun visibility(function: () -> KotlinModifiers.Visibility) {
        functionBuilder.addModifiers(function().toKModifier())
    }

    fun generateMethodBody(methodTemplate: CodeTemplate) {
        functionBuilder.addCode(methodTemplate.codeBlock)
    }

    fun generateMethodBody(format: String = "", vararg args: Any?, function: (CodeTemplate.() -> Unit)? = null) {
        val codeTemplate = CodeTemplate(format = format, args = args, function = function)
        functionBuilder.addCode(codeTemplate.codeBlock)
    }

    fun collectParameterTemplates(function: () -> Collection<ParameterTemplate>) {
        functionBuilder.addParameters(function().map { it.parameterSpec })
    }

    fun generateParameter(name: String, typeName: TypeName) {
        functionBuilder.addParameter(
            ParameterTemplate(name = name, typeName = typeName).parameterSpec
        )
    }
}

package com.casadetasha.kexp.generationdsl.dsl

import com.casadetasha.kexp.generationdsl.dsl.KotlinTemplate.toKModifier
import com.squareup.kotlinpoet.*

class FunctionTemplate(name: String,
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

    fun generateMethodBody(methodTemplate: CodeTemplate) {
        functionBuilder.addCode(methodTemplate.codeBlock)
    }

    fun generateMethodBody(format: String = "", vararg args: Any?, function: (CodeTemplate.() -> Unit)? = null) {
        val codeTemplate = CodeTemplate(format = format, args = args, function = function)
        functionBuilder.addCode(codeTemplate.codeBlock)
    }

    fun override() {
        functionBuilder.addModifiers(KModifier.OVERRIDE)
    }

    fun visibility(function: () -> KotlinTemplate.Visibility) {
        functionBuilder.addModifiers(function().toKModifier())
    }

    internal fun addParameter(parameterTemplate: ParameterTemplate) {
        functionBuilder.addParameter(parameterTemplate.parameterSpec)
    }

    internal fun addParameters(parameterTemplates: Collection<ParameterTemplate>) {
        functionBuilder.addParameters(parameterTemplates.map{ it.parameterSpec })
    }

    companion object {

        fun KotlinContainerTemplate.collectFunctionTemplates(function: KotlinContainerTemplate.() -> Collection<FunctionTemplate>) {
            function().forEach { addFunction(it) }
        }

        fun KotlinContainerTemplate.generateFunction(
            name: String,
            receiverType: ClassName? = null,
            returnType: ClassName? = null,
            function: FunctionTemplate.() -> Unit
        ) {
            addFunction(FunctionTemplate(name, receiverType, returnType, function))
        }
    }
}

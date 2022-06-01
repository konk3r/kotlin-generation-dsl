package com.casadetasha.kexp.generationdsl.dsl

import com.squareup.kotlinpoet.CodeBlock

class CodeTemplate(startingCodeBlock: CodeBlock? = null, function: (CodeTemplate.() -> Unit)? = null) {

    private val builder: CodeBlock.Builder = CodeBlock.builder()
    internal val codeBlock: CodeBlock

    constructor(format: String,
                vararg args: Any?,
                function: (CodeTemplate.() -> Unit)? = null
    ): this(CodeBlock.of(format, *args), function)

    init {
        startingCodeBlock?.let { builder.add(startingCodeBlock) }
        function?.let { this.function() }
        codeBlock = builder.build()
    }

    fun controlFlowCode(prefix: String, vararg prefixArgs: Any?,
                        suffix: String = "",
                        beginFlowString: String = "·{",
                        endFlowString: String = "}",
                        function: (CodeTemplate.() -> Unit)? = null) {
        builder.add(prefix, *prefixArgs)
        builder.add("$beginFlowString\n")
        builder.indent()

        function?.let { this.function() }

        builder.unindent()
        builder.add("${endFlowString}$suffix\n")
    }

    fun collectCodeTemplates(function: () -> Collection<CodeTemplate>) {
        function().forEach { template ->
            builder.add(template.codeBlock)
        }
    }

    fun collectCodeLines(function: () -> Collection<String>) {
        function().forEach { statement ->
            builder.addStatement(statement)
        }
    }

    fun collectCodeLineTemplates(function: () -> Collection<CodeTemplate>) {
        function().forEach { template ->
            codeLine(template)
        }
    }

    fun code(format: String, vararg args: Any?) {
        builder.add(format, args)
    }

    fun codeLine(format: String, vararg args: Any?) {
        codeLine(CodeTemplate(format, *args))
    }

    fun codeTemplate(function: () -> CodeTemplate) {
        builder.add(function().codeBlock)
    }

    private fun codeLine(template: CodeTemplate) {
        builder.add("«")
        builder.add(template.codeBlock)
        builder.add("\n»")
    }

    companion object {

        fun FunctionTemplate.methodBodyTemplate(function: CodeTemplate.() -> Unit) {
            methodBody { CodeTemplate(function = function) }
        }
    }
}

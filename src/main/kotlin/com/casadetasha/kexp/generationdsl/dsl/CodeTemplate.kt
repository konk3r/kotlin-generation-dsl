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
            generateCodeLine(template)
        }
    }

    fun generateCode(format: String, vararg args: Any?) {
        builder.add(format, *args)
    }

    fun generateCodeTemplate(function: () -> CodeTemplate) {
        builder.add(function().codeBlock)
    }

    fun generateNewLine() {
        builder.add("\n")
    }

    fun generateCodeLine(format: String, vararg args: Any?) {
        generateCodeLine(CodeTemplate(format, *args))
    }

    fun generateCodeLine(template: CodeTemplate) {
        builder.add("«")
        builder.add(template.codeBlock)
        builder.add("\n»")
    }

    fun generateControlFlowCode(prefix: String = "",
                                vararg prefixArgs: Any?,
                                suffix: String = "",
                                beginFlowString: String = "·{\n",
                                endFlowString: String = "\n}",
                                function: (CodeTemplate.() -> Unit)) {
        builder.add(prefix, *prefixArgs)
        builder.add(beginFlowString)
        builder.indent()

        this.function()

        builder.unindent()
        builder.add("${endFlowString}$suffix\n")
    }

    fun generateParenthesizedCodeBlock(prefix: String = "",
                                       vararg prefixArgs: Any?,
                                       suffix: String = "",
                                       function: (CodeTemplate.() -> Unit)) {
        generateControlFlowCode(prefix = prefix,
            prefixArgs = prefixArgs,
            suffix = suffix,
            beginFlowString = "(\n",
            endFlowString = "\n)",
            function = function
        )
    }
}

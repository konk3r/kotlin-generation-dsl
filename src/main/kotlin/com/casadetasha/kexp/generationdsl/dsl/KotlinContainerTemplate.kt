package com.casadetasha.kexp.generationdsl.dsl

interface KotlinContainerTemplate {

    fun addFunction(functionTemplate: FunctionTemplate)
    fun addProperties(properties: Collection<PropertyTemplate>)
}
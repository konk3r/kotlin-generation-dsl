package com.casadetasha.kexp.generationdsl.dsl

class ImportTemplate private constructor(
    internal val importPackage: String,
    internal val importName: String
){

    companion object {
        fun FileTemplate.importTemplate(importPackage: String, importName: String) {
            this.addImport(ImportTemplate(importPackage, importName))
        }
    }
}
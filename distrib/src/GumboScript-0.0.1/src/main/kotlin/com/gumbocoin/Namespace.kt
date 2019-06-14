package com.gumbocoin

open class GFunction(
    val name :String,
    val returnType :Type,
    val arguments :List<Argument>,
    val startingLine: Line,
    val isKotlin :Boolean)

class GumboFunction(
    name :String,
    returnType: Type,
    arguments: List<Argument>,
    val lines :CompiledBlock,
    startingLine :Line) : GFunction(name,returnType,arguments,startingLine,isKotlin = false){
    var parent :PartialNamespace? = null

    val fullName by lazy {
        var builder = ""
        var p = parent
        while(p != null){
            builder = p!!.name + ":" + builder
            p = p?.parent
        }
        "$builder:$name"
    }
}

class Namespace(
    val name :String,
    val subs :List<Namespace>,
    val functions :List<GFunction>)
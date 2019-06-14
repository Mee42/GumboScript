package com.gumbocoin

import com.xenomachina.argparser.*
import java.io.File

fun main(args: Array<String>){
    Parsed.nullable = mainBody { ArgParser(args, ArgParser.Mode.GNU,DefaultHelpFormatter(),"v0.1\n").parseInto(::Main) }
    run(Parsed.file.readText(Charsets.UTF_8))
}

object Parsed{
    internal var nullable :Main? = null
    val file :File
        get() = nullable!!.file


    val verbose :Boolean
        get() = nullable!!.verbose
    val debug :Boolean
        get() = nullable!!.debug

    val stackDepth :Int
        get() = nullable!!.stackDepth

    val stackTrace :Boolean
        get() = nullable!!.stacktrace

    val bigPrecision :Int
        get() = nullable!!.bigPrecision

    val crashKotlinOnKotlin :Boolean
        get() = nullable!!.crashKotlinOnKotlin
}

class Main(parser :ArgParser){

    val verbose by parser.flagging("-V","--verbose", help = "enable verbose mode during compile-time")

    val debug by parser.flagging("-d","--debug",help = "Print debug info during runtime")

    val stacktrace by parser.flagging("-s","--stacktrace",help = "Print kotlin stacktraces on crash")


    val file by parser.storing("-f","--file", help = "the file to use") { File(this) }

    val stackDepth by parser.storing("--stack-depth",help = "The max stack depth")
        { this.toInt() }.default(10_000)

    val bigPrecision by parser.storing("--big-precision",help ="The precision to use with the datatype big")
        { this.toInt() }.default(10_000)

    val crashKotlinOnKotlin by parser.flagging("--crash-kotlin-on-kotlin",help = "IDK just try it")


}
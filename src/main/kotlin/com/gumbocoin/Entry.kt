package com.gumbocoin

import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.default
import java.io.File

fun main(args: Array<String>) {
    Parsed.nullable = ArgParser(args).parseInto(::Main)
    run(Parsed.main.file.readText(Charsets.UTF_8))
}

object Parsed{
    var nullable :Main? = null
    val main :Main
        get() = nullable!!
}

class Main(parser :ArgParser){
    val v by parser.flagging("-v","--verbose", help = "enable verbose mode")

    val d by parser.flagging("-d","--debug",help = "Print debug info during runtime")

    val file by parser.storing("-f","--file", help = "the file to use") { File(this) }

    val stackDepth by parser.storing("--stack-depth",help = "The max stack depth")
        { this.toInt() }.default(10_000)

    val bigPrecision by parser.storing("--big-precision",help ="The precision to use with the datatype big")
        { this.toInt() }.default(10_000)

}
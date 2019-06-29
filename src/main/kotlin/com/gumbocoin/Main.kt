package com.gumbocoin

import com.gumbocoin.kotlin.generateKotlinNamespace
import com.gumbocoin.kotlin.generatePlaintextKotlinNamespace
import java.math.BigDecimal
import java.io.PrintWriter
import java.io.StringWriter



const val file = "res/prime.gumbo"


fun main() {
    val input = "-V -f $file"
    main(input.split(" ").toTypedArray())
}

fun throwException(t :Throwable){
    if(Parsed.stackTrace)
        throw t
}


fun verbose(s :String){
    if(Parsed.verbose)
        System.out.println(s)
}


fun debug(s :String){
    if(Parsed.debug)
        System.err.println("Debug: $s")
}


class Line(val content :String, val lineNumber :Int){
    override fun toString(): String {
        return "Line($lineNumber)"
    }
    companion object {
        private const val kotlinContent = "KOTLIN - LINE"
        private const val kotlinLineNumber = -2
        fun kotlin(): Line {
            return Line(kotlinContent, kotlinLineNumber)
        }
    }
}



object OUT{
    var impl :Output = object :Output {
        override fun print(s: String) {
            System.out.print(s)
        }

        override fun println(s: String) {
            System.out.println(s)
        }

        override fun error(s: String) {
            System.err.print(s)
        }

        override fun errorln(s: String) {
            System.err.println(s)
        }

        override fun stacktrace(t: Throwable) {
            t.printStackTrace()
        }
    }
    fun print(s :String) = impl.print(s)
    fun println(s :String) = impl.println(s)
    fun error(s :String) = impl.error(s)
    fun errorln(s :String) = impl.errorln(s)
    fun stacktrace(t :Throwable) = impl.stacktrace(t)
}

interface Output {
    fun print(s :String)
    fun println(s :String)
    fun error(s :String)
    fun errorln(s :String)
    fun stacktrace(t :Throwable)
}



fun run(script :String) {
    val processed = preprocess(script)
    val gumboNamespace = initialNamespaceParse(processed,"gumbo")

    val partialNamespace = compileNamespace(gumboNamespace)

    val allPartial = listOf(partialNamespace, generatePlaintextKotlinNamespace())


    val finalNamespace = finalCompileNamespace(partialNamespace,allPartial)
    val kotlinNamespace = generateKotlinNamespace()
    val allFull = listOf(finalNamespace,kotlinNamespace)

    verbose("    ====    execution    ====    \n")
    execute(finalNamespace,allFull)
}


fun stringify(n :Any):String{
    return when(n){
        is BigDecimal -> n.toPaddedString()
        else -> n.toString()
    }
}
//TODO make sure this is used in the right places
fun BigDecimal.toPaddedString():String{
    var str = toPlainString()
    while(str.length > 2 && str[str.lastIndex] == '0') str = str.substring(0,str.length - 1)
    str + if(str[str.lastIndex] == '.') "0" else ""
    return str
}

const val LOWERCASE = "abcdefghijklmnopqrstuvwxyz"
const val UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
const val NUMBERS = "0123456789"
const val EXTRA = "_"


val INVALID_IDENTIFIERS = listOf(
    "if","while","fun")

fun checkValidIdentifier(name :String, line :Line){
    if(isValidIdentifier(name)) {

        compileError("Identifier $name is invalid on line ${line.lineNumber}")
    }
}

fun isValidIdentifier(name :String):Boolean{
    return name.isBlank() ||
            !(LOWERCASE + UPPERCASE).contains(name[0]) ||
            name.any { !(LOWERCASE + UPPERCASE + NUMBERS + EXTRA).contains(it)} ||
            INVALID_IDENTIFIERS.contains(name)
}

class CompileException(message :String) :Exception(message)

//@Throws(CompileException::class)
fun compileError(message: String):Nothing{
    throw CompileException(message)
}

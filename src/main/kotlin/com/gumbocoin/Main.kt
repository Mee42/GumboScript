package com.gumbocoin

import java.io.File
import java.math.BigDecimal

const val file = "res/test.gumbo"

object GConstants{
    const val BIG_DECIMAL_SCALE = 10_000
}

fun main() {
    File("./out").deleteRecursively()
    File("./out").mkdir()

    val text = File(file).readText(Charsets.UTF_8)
    run(text)
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


fun run(script :String) {
    val processed = preprocess(script)
    val gumboNamespace = initialNamespaceParse(processed,"gumbo")

    val partialNamespace = compileNamespace(gumboNamespace)

    val allPartial = listOf(partialNamespace, generatePlaintextKotlinNamespace())


    val finalNamespace = finalCompileNamespace(partialNamespace,allPartial)
    val kotlinNamespace = generateKotlinNamespace()
    val allFull = listOf(finalNamespace,kotlinNamespace)

    println("    ====    execution    ====    \n")
    execute(finalNamespace,allFull)
}


fun stringify(n :Any):String{
    return when(n){
        is BigDecimal -> n.toPlainString()
        else -> n.toString()
    }
}

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
